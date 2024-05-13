package com.nombreGrupo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;

import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.modelo.entities.Pedido.MetodoEnvio;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.LineaFacturacionRepository;
//import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.repositories.PedidoRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.repositories.UsuarioRepository;
import com.nombreGrupo.util.EmailUtil;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PedidoServiceImplMy8 implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
	@Autowired
	private ModelMapper modeloMapper;
    @Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private LineaFacturacionRepository lineaFacturacionRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;

    @Override
    public List<Pedido> encontrarTodos() {
        return pedidoRepository.findAll();
    }
    
	@Override
	public Page<Pedido> encontrarTodos(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }
    
    @Override
    public Pedido encontrarPorId(int idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("No existe un pedido de idPedido " +idPedido+ "."));
    }
   
    @Override
    public List<Pedido> encontrarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }
    
    @Override
    public List<Pedido> encontrarPorPais(String ciudad) {
        return pedidoRepository.findByCiudad(ciudad);
    }
    @Override
    public List<Pedido> encontrarPorPrecioTotalMayorOIgualQue(double precio) {
        return pedidoRepository.findByPrecioTotalGreaterThanEqual(precio);  // Usando método de consulta derivada
    }

    @Override
    @Transactional
    public Pedido crearYGuardarConLF(int idUsuario, List<Integer> productoIds, List<Integer> cantidades, String nombre, String apellidos, String direccion, String pais, String ciudad, String numeroTelefonoMovil, MetodoEnvio metodoEnvio) {
        
    	//Si no existe usuario con ese idUsuario lanzar expceción adviertiendo de ello.
    	Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("No existe usuario con IdUsuario: "+idUsuario+"."));
    	
    	//Si el usuario existe pero no está activo (no ha verificado correo electrónico) lanzar expceción advirtiendo de ello.
        if (usuario.getActive()==false) {
            throw new IllegalStateException("El usuario con IdUsuario: "+idUsuario+" no está activo, luego no puede hacer pedidos.");
        }
    	
    	//Si no existe suficiente stock para algún producto lanzar expceción adviertiendo de ello.
        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoRepository.findById(productoIds.get(i)).orElseThrow();
            if (producto.getStock() < cantidades.get(i)) {
                throw new IllegalStateException("No hay suficiente stock para el producto: "+producto.getNombre()+".");
            }
        }
    	
    	Pedido pedido = new Pedido();
    	pedido.setUsuario(usuarioRepository.findById(idUsuario).orElseThrow(() -> new EntityNotFoundException("No existe usuario con ID: " +idUsuario+".")));
        pedido.setNombre(nombre);
        pedido.setApellidos(apellidos);
        pedido.setDireccion(direccion);
        pedido.setPais(pais);
        pedido.setCiudad(ciudad);
        pedido.setNumeroTelefonoMovil(numeroTelefonoMovil);
        pedido.setMetodoEnvio(metodoEnvio);
        pedido.setEstado(Pedido.EstadoPedido.pendiente);
        pedidoRepository.save(pedido);
        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoRepository.findById(productoIds.get(i)).orElseThrow();
            LineaFacturacion lf = new LineaFacturacion();
            lf.setProducto(producto);
            lf.setPedido(pedido);
            lf.setCantidad(cantidades.get(i));
            lf.setEstado(LineaFacturacion.EstadoLineaFacturacion.activo);
            lineaFacturacionRepository.save(lf);
        }
        return pedido;
    }
    
	@Override
	public Pedido actualizarSinCambiarLF(int idPedido, PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion) {
		Pedido pedidoExistente = pedidoRepository.findById(idPedido)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe pedido de idpedido "+idPedido+"."));
		Pedido pedido = new Pedido();
		modeloMapper.map(pedidoDtoActualizacion, pedido);
		pedido.setIdPedido(idPedido);
		pedido.setUsuario(pedidoExistente.getUsuario());
		pedido.setMetodoEnvio(pedidoExistente.getMetodoEnvio());
		 if (!pedidoExistente.getEstado().equals(Pedido.EstadoPedido.cancelado) && pedidoDtoActualizacion.getEstado().equals(Pedido.EstadoPedido.cancelado)) {
		     pedidoExistente.setEstado(Pedido.EstadoPedido.cancelado);
		        
		     // Actualizar las líneas de facturación asociadas a este pedido
		     List<LineaFacturacion> lineasFacturacion = lineaFacturacionRepository.findByPedido_IdPedido(idPedido);
		     for (LineaFacturacion lf : lineasFacturacion) {
		         lf.setEstado(LineaFacturacion.EstadoLineaFacturacion.cancelado);
		         Producto productoDeLalinea = lf.getProducto();
		         productoDeLalinea.setStock(productoDeLalinea.getStock()+lf.getCantidad());
		         productoRepository.save(productoDeLalinea);
		         lineaFacturacionRepository.save(lf);
		     }
		 } else if (pedidoExistente.getEstado().equals(Pedido.EstadoPedido.cancelado) && pedidoDtoActualizacion.getEstado().equals(Pedido.EstadoPedido.pendiente)) {
		     pedidoExistente.setEstado(Pedido.EstadoPedido.pendiente);

		     // Actualizar las líneas de facturación asociadas a este pedido
		     List<LineaFacturacion> lineasFacturacion = lineaFacturacionRepository.findByPedido_IdPedido(idPedido);
		     for (LineaFacturacion lf : lineasFacturacion) {
		         lf.setEstado(LineaFacturacion.EstadoLineaFacturacion.activo);
		         Producto productoDeLalinea = lf.getProducto();
		         productoDeLalinea.setStock(productoDeLalinea.getStock()-lf.getCantidad());
		         productoRepository.save(productoDeLalinea);
		         lineaFacturacionRepository.save(lf);
		     }
		 }
	     
		return pedidoRepository.save(pedido);
	}

	@Override
    public boolean borrarPorId(int id) {
		//Maneja el hecho de que no se pueda borrar por ser clave ajena
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
	
	@Override
    public List<Pedido> encontrarPorUsuarioIdUsuario(int idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario);
    }
    
}
