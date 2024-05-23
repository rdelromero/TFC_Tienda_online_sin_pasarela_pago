package com.nombreGrupo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.CategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Categoria;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.repositories.CategoriaRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.util.StringUtil;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaServiceImplMy8 implements CategoriaService{

    @Autowired
    private CategoriaRepository categoriaRepository;
	@Autowired
    private ModelMapper modeloMapper;
    @Autowired
    private ProductoRepository productoRepository;
    
	@Override
	public List<Categoria> encontrarTodas() {
		return categoriaRepository.findAll();
	}

	@Override
	public Categoria encontrarPorId(int idCategoria) {
		return categoriaRepository.findById(idCategoria)
				.orElseThrow(() -> new EntityNotFoundException("No existe una categoria de idCcategoria " + idCategoria + "."));
	}

    @Override
    public Categoria encontrarPorNombre(String nombre) {
        String nombreKebab = StringUtil.toKebabCase(nombre);
        List<Categoria> todasCategorias = categoriaRepository.findAll();
        for (Categoria categoria : todasCategorias) {
            if (StringUtil.toKebabCase(categoria.getNombre()).equals(nombreKebab)) {
                return categoria;
            }
        }
        throw new EntityNotFoundException("No existe una subcategoria de nombre " + nombre+".");
    }
	
	@Override
	public Categoria crearYGuardar(CategoriaDtoCreacion categoriadtocreacion) {
		Categoria categoria = new Categoria();
		modeloMapper.map(categoriadtocreacion, categoria);
		return categoriaRepository.save(categoria);
	}

	@Override
	public Categoria actualizar(int idCategoria, CategoriaDtoCreacion categoriadtoactualizacion) {
		categoriaRepository.findById(idCategoria)
		.orElseThrow(() -> new EntityNotFoundException("No existe una categoría de idCcategoria " + idCategoria + "."));

		Categoria categoria = new Categoria();
		modeloMapper.map(categoriadtoactualizacion, categoria);
		categoria.setIdCategoria(idCategoria);
		return categoriaRepository.save(categoria);
	}

	@Override
	public boolean borrarPorId(int idCategoria) {
		categoriaRepository.deleteById(idCategoria);
		return true;
	}
	
	@Override
    public List<Producto> encontrarProductosPorSubcategoriaCategoriaIdCategoria(int idCategoria) {
        return productoRepository.findBySubcategoriaCategoriaIdCategoria(idCategoria);
    }
}
