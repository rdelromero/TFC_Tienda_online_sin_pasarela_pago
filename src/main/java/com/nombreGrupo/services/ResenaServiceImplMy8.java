package com.nombreGrupo.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.LineaFacturacionDto;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;
import com.nombreGrupo.modelo.dto.ResenaDtoCreacion;
import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Resena;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.repositories.ResenaRepository;
import com.nombreGrupo.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
@Service
public class ResenaServiceImplMy8 implements ResenaService{
	
	@Autowired
	private ResenaRepository resenaRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private ModelMapper modeloMapper;
	
    @Override
    public List<Resena> encontrarTodas() {
        return resenaRepository.findAll();
    }
    
    @Override
    public Resena crearYGuardar(ResenaDtoCreacion resenaDtoCreacion) {
        
        // Si no existe usuario con ese idUsuario lanzar excepción advirtiendo de ello.
    	Usuario usuario = usuarioRepository.findById(resenaDtoCreacion.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("No existe usuario con IdUsuario: " + resenaDtoCreacion.getIdUsuario() + "."));
        
        // Si el usuario existe pero no está activo (no ha verificado correo electrónico) lanzar excepción advirtiendo de ello.
        if (!usuario.isEnabled()) {
            throw new IllegalStateException("El usuario con IdUsuario: " + usuario.getIdUsuario() + " no está activo, luego no puede hacer reseñas.");
        }
        
    	productoRepository.findById(resenaDtoCreacion.getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("No existe producto con IdProducto: " + resenaDtoCreacion.getIdProducto() + "."));
 
    	//Si a valoración se le pone número decimal dará error antes de llegar aquí porque en la entity Resena valoración debe ser un entero
    	// Verificar que la valoración es un entero comprendido entre 1 y 5
    	if (resenaDtoCreacion.getValoracion() < 1 || resenaDtoCreacion.getValoracion() > 5) {
            throw new IllegalArgumentException("Has introducido un entero inválido. El entero debe estar comprendido entre 1 y 5.");
        }
        
        Resena resena = new Resena();
        modeloMapper.map(resenaDtoCreacion, resena);
        return resenaRepository.save(resena);
    }
    
    //Sin usar porque pusimos un campo numeroValoraciones en la entidad
    /*@Override
    public int encontrarNumeroResenasPorIdProducto(int idProducto) {
        return resenaRepository.contarResenasPorIdProducto(idProducto);
    }*/
    
	@Override
	public boolean borrarPorId(int idResena) {
	    // Verificar si el producto existe
	    Resena resena = resenaRepository.findById(idResena)
	            .orElseThrow(() -> new EntityNotFoundException("No existe resena con ID: "+idResena+"."));

	    //Actualizar del producto al que hacía mención la reseña el campo numeroValoraciones
	    int valoracionDeLaResena = resena.getValoracion();
	    Producto productoDeLaResena= resena.getProducto();
	    int numeroValoracionesActualizado = productoDeLaResena.getNumeroValoraciones() - 1;
	    productoDeLaResena.setNumeroValoraciones(numeroValoracionesActualizado);
	    
	  //Actualizar del producto al que hacía mención la reseña el campo valoracionMedia
	    if (numeroValoracionesActualizado == 0) {
	    	productoDeLaResena.setValoracionMedia(null);
	    } else if (numeroValoracionesActualizado>0) {
	    	double valoracionMediaActualizada = (productoDeLaResena.getValoracionMedia()*(numeroValoracionesActualizado+1)-valoracionDeLaResena)/numeroValoracionesActualizado;
	    	productoDeLaResena.setValoracionMedia(valoracionMediaActualizada);
	    }
	    productoRepository.save(productoDeLaResena);
	    resenaRepository.deleteById(idResena);
	    return true;
	}
}
