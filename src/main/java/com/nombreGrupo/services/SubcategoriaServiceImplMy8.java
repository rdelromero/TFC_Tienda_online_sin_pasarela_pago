package com.nombreGrupo.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.SubcategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.repositories.SubcategoriaRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.util.StringUtil;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubcategoriaServiceImplMy8 implements SubcategoriaService{

	@Autowired
	private SubcategoriaRepository subcategoriaRepository;
	@Autowired
    private ModelMapper modeloMapper;
	@Autowired
	private ProductoRepository productoRepository;
	
	@Override
	public List<Subcategoria> encontrarTodas() {
		return subcategoriaRepository.findAll();
	}

	@Override
	public Page<Subcategoria> encontrarTodasPaginacion(Pageable pageable) {
		return subcategoriaRepository.findAll(pageable);
	}
	
	@Override
	public Subcategoria encontrarPorId(int idSubcategoria) {
		return subcategoriaRepository.findById(idSubcategoria)
				.orElseThrow(() -> new EntityNotFoundException("No existe una subcategoria de idSubcategoria " + idSubcategoria + "."));
	}
	
    @Override
    public Subcategoria encontrarPorNombre(String nombre) {
        String nombreKebab = StringUtil.toKebabCase(nombre);
        List<Subcategoria> todasSubcategorias = subcategoriaRepository.findAll();
        for (Subcategoria subcategoria : todasSubcategorias) {
            if (StringUtil.toKebabCase(subcategoria.getNombre()).equals(nombreKebab)) {
                return subcategoria;
            }
        }
        throw new EntityNotFoundException("No existe una subcategoria de nombre " + nombre+".");
    }
    
	@Override
	public Subcategoria crearYGuardar(SubcategoriaDtoCreacion subcategoriadtocreacion) {
		Subcategoria subcategoria = new Subcategoria();
		modeloMapper.map(subcategoriadtocreacion, subcategoria);
		return subcategoriaRepository.save(subcategoria);
	}
    
	@Override
	public Subcategoria actualizar(int idSubcategoria, SubcategoriaDtoCreacion subcategoriadtoactualizacion) {
		subcategoriaRepository.findById(idSubcategoria)
		.orElseThrow(() -> new EntityNotFoundException("No existe una subcategoría de idSubcategoria " + idSubcategoria + "."));
		
		Subcategoria subcategoria = new Subcategoria();
		modeloMapper.map(subcategoriadtoactualizacion, subcategoria);
		subcategoria.setIdSubcategoria(idSubcategoria);
		return subcategoriaRepository.save(subcategoria);
	}
	
	@Override
	public boolean borrarPorId(int idSubcategoria) {
		subcategoriaRepository.findById(idSubcategoria).orElseThrow(() -> new EntityNotFoundException("No existe una subcategoria de idSubcategoria " + idSubcategoria + "."));
		subcategoriaRepository.deleteById(idSubcategoria);
		return true;
	}
	
	@Override
    public List<Producto> encontrarProductosPorSubcategoria_IdSubcategoria(int idSubcategoria) {
        return productoRepository.findBySubcategoria_IdSubcategoria(idSubcategoria);
    }
}
