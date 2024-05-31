package com.nombreGrupo.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;

import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;

import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.LineaFacturacionRepository;
import com.nombreGrupo.modelo.dto.LineaFacturacionDto;
import com.nombreGrupo.repositories.PedidoRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.repositories.UsuarioRepository;

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
    public Pedido crearYGuardarConLF(PedidoDtoCreacionConLineasFacturacion pedidoDtoCreacionConLF) {
        
        // Si no existe usuario con ese idUsuario lanzar excepción advirtiendo de ello.
    	Usuario usuario = usuarioRepository.findById(pedidoDtoCreacionConLF.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("No existe usuario con IdUsuario: " + pedidoDtoCreacionConLF.getIdUsuario() + "."));
        
        // Si el usuario existe pero no está activo (no ha verificado correo electrónico) lanzar excepción advirtiendo de ello.
        if (!usuario.isEnabled()) {
            throw new IllegalStateException("El usuario con IdUsuario: " + usuario.getIdUsuario() + " no está activo, luego no puede hacer pedidos.");
        }
        
        List<LineaFacturacionDto> lineasFacturacionDto = pedidoDtoCreacionConLF.getLineasFacturacionDto();
        
        // Verificar que la lista de líneas de facturación no sea nula o vacía
        if (lineasFacturacionDto == null || lineasFacturacionDto.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos una línea de facturación.");
        }
        
        // Validar el stock para cada producto en las líneas de facturación
        for (LineaFacturacionDto lineaDto : lineasFacturacionDto) {
            Producto producto = productoRepository.findById(lineaDto.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("No existe producto con IdProducto: " + lineaDto.getIdProducto() + "."));
            
            if (producto.getStock() < lineaDto.getCantidad()) {
                throw new IllegalStateException("No hay suficiente stock para el producto: " + producto.getNombre() + ".");
            }
        }
        
        Pedido pedido = new Pedido();
        
        modeloMapper.map(pedidoDtoCreacionConLF, pedido);
        pedido.setEstado(Pedido.EstadoPedido.pendiente);
        pedido = pedidoRepository.save(pedido);
        
        // Crear y guardar las líneas de facturación
        for (LineaFacturacionDto lineaDto : lineasFacturacionDto) {
            Producto producto = productoRepository.findById(lineaDto.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("No existe producto con IdProducto: " + lineaDto.getIdProducto() + "."));
            
            LineaFacturacion lineaFacturacion = new LineaFacturacion();
            
            modeloMapper.map(lineaDto, lineaFacturacion);
            
            lineaFacturacion.setPedido(pedido);
            lineaFacturacion.setEstado(LineaFacturacion.Estado.activo);
            
            // Actualizar el stock del producto
            producto.setStock(producto.getStock() - lineaDto.getCantidad());
            productoRepository.save(producto);
            
            lineaFacturacionRepository.save(lineaFacturacion);
        }
        
        System.out.println("jajaja"+pedido);
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
		         lf.setEstado(LineaFacturacion.Estado.cancelado);
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
		         lf.setEstado(LineaFacturacion.Estado.activo);
		         Producto productoDeLalinea = lf.getProducto();
		         productoDeLalinea.setStock(productoDeLalinea.getStock()-lf.getCantidad());
		         productoRepository.save(productoDeLalinea);
		         lineaFacturacionRepository.save(lf);
		     }
		 }
	     
		return pedidoRepository.save(pedido);
	}

	@Override
	public Pedido actualizarSinCambiarLFUsuario(int idUsuarioDelJwt, int idPedido, PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion) {
		
		usuarioRepository.findById(idUsuarioDelJwt)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe usuario de idUsuario "+idUsuarioDelJwt+"."));
		
		Pedido pedidoExistente = pedidoRepository.findById(idPedido)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe pedido de idPedido "+idPedido+"."));
		
        if (idUsuarioDelJwt!=pedidoExistente.getUsuario().getIdUsuario()) {
            throw new IllegalStateException("El usuario del pedido que quieres actualizar no coincide con el usuario del JWT.");
        }
		
        if (pedidoExistente.getEstado()!=Pedido.EstadoPedido.pendiente) {
            throw new IllegalStateException("El pedido con IdPedido: "+idPedido+" no está pendiente de tramitar. No puede realizar cambios. Contacte con la tienda para reportar posibles cambios.");
        }
		
		Pedido pedido = new Pedido();
		modeloMapper.map(pedidoDtoActualizacion, pedido);
		pedido.setIdPedido(idPedido);
		pedido.setUsuario(pedidoExistente.getUsuario());
		pedido.setMetodoEnvio(pedidoExistente.getMetodoEnvio());
		
		if (pedidoExistente.getEstado().equals(Pedido.EstadoPedido.pendiente) && pedidoDtoActualizacion.getEstado().equals(Pedido.EstadoPedido.cancelado)) {
  
		     // Actualizar las líneas de facturación asociadas a este pedido
		     List<LineaFacturacion> lineasFacturacion = lineaFacturacionRepository.findByPedido_IdPedido(idPedido);
		     for (LineaFacturacion lf : lineasFacturacion) {
		         lf.setEstado(LineaFacturacion.Estado.cancelado);
		         Producto productoDeLalinea = lf.getProducto();
		         productoDeLalinea.setStock(productoDeLalinea.getStock()+lf.getCantidad());
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
    public List<Pedido> encontrarPorUsuario_IdUsuario(int idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario);
    }
    
}
