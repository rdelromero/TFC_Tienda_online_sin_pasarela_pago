package com.nombreGrupo.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.repositories.FabricanteRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.util.StringUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FabricanteServiceImplMy8 implements FabricanteService{

	@Autowired
	private FabricanteRepository fabricanteRepository;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private ModelMapper modeloMapper;
	
	@Override
	public List<Fabricante> encontrarTodos() {
		return fabricanteRepository.findAll();
	}
	
    @Override
    public Page<Fabricante> encontrarTodosPaginacion(Pageable pageable) {
        return fabricanteRepository.findAll(pageable);
    }
	
    @Override
    public Fabricante encontrarPorId(int idFabricante) {
        return fabricanteRepository.findById(idFabricante)
                .orElseThrow(() -> new EntityNotFoundException("No existe un fabricante de idFabricante " +idFabricante+ "."));
    }
	
    @Override
    public Fabricante encontrarPorNombre(String nombre) {
        String nombreKebab = StringUtil.toKebabCase(nombre);
        List<Fabricante> todosFabricantes = fabricanteRepository.findAll();
        for (Fabricante fabricante : todosFabricantes) {
            if (StringUtil.toKebabCase(fabricante.getNombre()).equals(nombreKebab)) {
                return fabricante;
            }
        }
        throw new EntityNotFoundException("No existe un fabricante con nombre " + nombre+".");
    }
    
	@Override
    public List<Producto> encontrarProductosPorFabricante_IdFabricante(int idFabricante) {
        return productoRepository.findByFabricante_IdFabricante(idFabricante);
    }
    
	@Override
	public Fabricante crearYGuardar(FabricanteDtoCreacion fabricanteDtoCreacion) {
	    Fabricante fabricante = new Fabricante();
	    modeloMapper.map(fabricanteDtoCreacion, fabricante);
	    return fabricanteRepository.save(fabricante);
	}
	
	@Override
	public Fabricante actualizar(int idFabricante, FabricanteDtoCreacion fabricanteDtoCreacion) {
    	fabricanteRepository.findById(idFabricante)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe fabricante de idFabricante "+idFabricante+"."));
		
		Fabricante fabricante = new Fabricante();
		modeloMapper.map(fabricanteDtoCreacion, fabricante);
		fabricante.setIdFabricante(idFabricante);
		return fabricanteRepository.save(fabricante);
	}
	
    @Override
    public boolean borrarPorId(int idFabricante) {
    	
    	fabricanteRepository.findById(idFabricante)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe fabricante de idFabricante "+idFabricante+"."));
    	
        if (productoRepository.existsByFabricante_IdFabricante(idFabricante)) {
            throw new IllegalStateException("Imposible borrar el fabricante. Hay productos de este fabricante.");
        }
    	
        fabricanteRepository.deleteById(idFabricante);
            return true;
    }
    
    @Override
    public void eliminarFabricanteConProductos(int idFabricante) {
        // Encuentra todos los productos asociados al fabricante
        List<Producto> productos = productoRepository.findByFabricante_IdFabricante(idFabricante);
        
        // Elimina todos los productos asociados
        productoRepository.deleteAll(productos);

        // Finalmente, elimina el fabricante
        fabricanteRepository.deleteById(idFabricante);
    }
}
