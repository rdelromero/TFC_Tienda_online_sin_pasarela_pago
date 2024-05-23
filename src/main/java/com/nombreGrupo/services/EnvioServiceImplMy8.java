package com.nombreGrupo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.EnvioDtoCreacion;
import com.nombreGrupo.modelo.dto.EnvioDtoActualizacion;
import com.nombreGrupo.modelo.entities.Envio;
import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.repositories.EnvioRepository;
import com.nombreGrupo.repositories.LineaFacturacionRepository;
import com.nombreGrupo.repositories.PedidoRepository;
import com.nombreGrupo.repositories.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
@Service
public class EnvioServiceImplMy8 implements EnvioService {

    @Autowired
    private EnvioRepository envioRepository;
	
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private LineaFacturacionRepository lineaFacturacionRepository;
    
	@Autowired
	private ModelMapper modeloMapper;
    
	@Override
	public List<Envio> encontrarTodos() {
		return envioRepository.findAll();
	}

	@Override
	public Page<Envio> encontrarTodos(Pageable pageable) {
        return envioRepository.findAll(pageable);
    }
	
	@Override
	public Envio encontrarPorId(int idEnvio) {
		 return envioRepository.findById(idEnvio)
	         .orElseThrow(() -> new EntityNotFoundException("No existe un envío de idEnvio " +idEnvio+ "."));
	}

	@Transactional
	@Override
	public Envio crearYGuardar(EnvioDtoCreacion envioDtoCreacion) {
		Envio envioVacio = new Envio();
		modeloMapper.map(envioDtoCreacion, envioVacio);
		Envio envio = envioRepository.save(envioVacio);
		Pedido pedido = pedidoRepository.findById(envio.getPedido().getIdPedido()).orElse(null);
		pedido.setEstado(Pedido.EstadoPedido.enviado);
		pedidoRepository.save(pedido);
		return envio;
	}

	@Override
	public Envio actualizar(int idEnvio, EnvioDtoActualizacion envioDtoActualizacion) {
		
    	envioRepository.findById(idEnvio)
        .orElseThrow(() -> new EntityNotFoundException("No existe envío de idEnvio "+idEnvio+"."));
		
		Envio envioVacio = new Envio();
		modeloMapper.map(envioDtoActualizacion, envioVacio);
		envioVacio.setIdEnvio(idEnvio);
		envioVacio.setPedido(envioRepository.findById(idEnvio).get().getPedido());
		Envio envio = envioRepository.save(envioVacio);

		Pedido pedido = pedidoRepository.findById(envio.getPedido().getIdPedido()).orElse(null);
		
		if (pedido.getEstado().equals(Pedido.EstadoPedido.enviado) && envio.getFechaEntrega()!=null) {
			pedido.setEstado(Pedido.EstadoPedido.entregado);
			pedidoRepository.save(pedido);
		}
		if (pedido.getEstado().equals(Pedido.EstadoPedido.enviado) && envio.getFechaEntregaVueltaAlmacen()!=null) {
			pedido.setEstado(Pedido.EstadoPedido.cancelado);
			pedidoRepository.save(pedido);
		     List<LineaFacturacion> lineasFacturacion = lineaFacturacionRepository.findByPedido_IdPedido(pedido.getIdPedido());
		     for (LineaFacturacion lf : lineasFacturacion) {
		         lf.setEstado(LineaFacturacion.Estado.cancelado);
		         Producto productoDeLalinea = lf.getProducto();
		         productoDeLalinea.setStock(productoDeLalinea.getStock()+lf.getCantidad());
		         productoRepository.save(productoDeLalinea);
		         lineaFacturacionRepository.save(lf);
		     }
		}
		
		return envio;
	}

	@Override
	public boolean borrarPorId(int id) {
        if (envioRepository.existsById(id)) {
            envioRepository.deleteById(id);
            return true;
        }
        return false;
	}

}
