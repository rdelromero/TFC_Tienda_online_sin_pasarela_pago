package com.nombreGrupo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Resena;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.repositories.ResenaRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class ResenaServiceImplMy8 implements ResenaService{
	
	@Autowired
	private ResenaRepository resenaRepository;
	@Autowired
	private ProductoRepository productoRepository;
	
    @Override
    public List<Resena> encontrarTodas() {
        return resenaRepository.findAll();
    }
    
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
