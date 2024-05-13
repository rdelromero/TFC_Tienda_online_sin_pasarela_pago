package com.nombreGrupo.especificaciones;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProductoEspecificaciones {

	//Búsqueda por una palabra
	/*public static Specification<Producto> tienePalabraClave(String palabra) {
        return (root, query, criteriaBuilder) -> {
            if (palabra == null || palabra.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Siempre verdadero si no hay palabra
            }
            String pattern = "%" + palabra.toLowerCase() + "%";
            Predicate nombreMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), pattern);
            Predicate fabricanteMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("fabricante").get("nombre")), pattern);
            return criteriaBuilder.or(nombreMatch, fabricanteMatch);
        };
    }*/
	
	//Búsqueda por varias palabras
    public static Specification<Producto> contienePalabrasClave(String palabras) {
        return (Root<Producto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (palabras == null || palabras.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Siempre verdadero si no hay palabras
            }
            String[] palabrasArray = palabras.trim().split("\\s+"); // Dividir por espacios y manejar múltiples espacios
            Predicate finalPredicate = criteriaBuilder.disjunction(); // Crear un predicado OR

            // Iterar sobre cada palabra clave
            for (String palabra : palabrasArray) {
                String pattern = "%" + palabra.toLowerCase() + "%";
                Predicate nombreMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), pattern);
                Predicate fabricanteMatch = criteriaBuilder.like(criteriaBuilder.lower(root.join("fabricante").get("nombre")), pattern);
                Predicate subcategoriaMatch = criteriaBuilder.like(criteriaBuilder.lower(root.join("subcategoria").get("nombre")), pattern);
	            Predicate categoriaMatch = criteriaBuilder.like(criteriaBuilder.lower(root.join("subcategoria").join("categoria").get("nombre")), pattern);
	            
	            // Combinar todos los predicados para la palabra actual
	            Predicate combined = criteriaBuilder.or(nombreMatch, fabricanteMatch, subcategoriaMatch, categoriaMatch);
                
                finalPredicate = criteriaBuilder.or(finalPredicate, combined);
            }

            return finalPredicate;
        };
    }
    
    //Búsqueda por varias palabras y filtro
    public static Specification<Producto> buscaYFiltra(
            Integer idCategoria, TipoDescuento tipoDescuento, 
            Integer idFabricante, double precioMinimo, double precioMaximo, String palabras) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtros actuales
            if (idCategoria != null) {
                predicates.add(criteriaBuilder.equal(root.get("subcategoria").get("categoria").get("idCategoria"), idCategoria));
            }
            if (tipoDescuento != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoDescuento"), tipoDescuento));
            }
            if (idFabricante != null) {
                predicates.add(criteriaBuilder.equal(root.get("fabricante").get("idFabricante"), idFabricante));
            }
            predicates.add(criteriaBuilder.between(root.get("precioFinal"), precioMinimo, precioMaximo));

            // Búsqueda por palabras clave
            if (palabras != null && !palabras.isEmpty()) {
                String[] palabrasArray = palabras.trim().split("\\s+");
                Predicate disjunction = criteriaBuilder.disjunction();
                for (String palabra : palabrasArray) {
                    String pattern = "%" + palabra.toLowerCase() + "%";
                    Predicate nombreMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), pattern);
                    Predicate fabricanteMatch = criteriaBuilder.like(criteriaBuilder.lower(root.join("fabricante").get("nombre")), pattern);
                    disjunction = criteriaBuilder.or(disjunction, nombreMatch, fabricanteMatch);
                }
                predicates.add(disjunction);
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
