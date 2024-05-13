package com.nombreGrupo.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.repositories.FabricanteRepository;
import com.nombreGrupo.repositories.ProductoRepository;

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
}
