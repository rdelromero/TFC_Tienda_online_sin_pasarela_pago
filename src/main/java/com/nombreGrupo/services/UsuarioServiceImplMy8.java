package com.nombreGrupo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.repositories.PedidoRepository;
import com.nombreGrupo.repositories.UsuarioRepository;
import com.nombreGrupo.util.EmailUtil;
import com.nombreGrupo.repositories.VerificacionUuidRepository;
import com.nombreGrupo.modelo.entities.VerificacionUuid;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioServiceImplMy8 implements UsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PedidoRepository pedidoRepository;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private ModelMapper modeloMapper;
	@Autowired
    private VerificacionUuidRepository verificacionUuidRepository;
	
    @Override
    public List<Usuario> encontrarTodos() {
        return usuarioRepository.findAll();
    }

	@Override
	public Page<Usuario> encontrarTodosPaginacion(Pageable pageable) {
		return usuarioRepository.findAll(pageable);
	}
    
    @Override
    public List<Usuario> encontrarPorActiveTrue() {
        return usuarioRepository.findByActiveTrue();
    }
    
    @Override
    public Usuario encontrarPorId(int idUsuario) {
        return usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new EntityNotFoundException("No existe un usuario de id " + idUsuario + "."));
    }

    @Override
    public Usuario encontrarPorDireccionEmail(String direccionEmail) {
  	  Optional<Usuario> usuario = usuarioRepository.findByDireccionEmail(direccionEmail);
        if (!usuario.isPresent()) {
         	throw new RuntimeException("No existe un usuario de dirección email "+direccionEmail+".");
          }
          return usuario.get();  // Devuelve el usuario encontrado
    }
    
	@Override
    public List<Pedido> encontrarPedidosPorUsuario_IdUsuario(int idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario);
    }
    
    @Override
    public Usuario crearYGuardar(UsuarioDtoRegistro usuarioDtoRegistro) {
    	
        if (usuarioRepository.findByDireccionEmail(usuarioDtoRegistro.getDireccionEmail()).isPresent()) {
            throw new DataIntegrityViolationException("No se ha podido crear el usuario. Ya existe un usuario con la dirección de correo electrónico " + usuarioDtoRegistro.getDireccionEmail() + ".");
        }
    	
        if (usuarioDtoRegistro.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        
        Usuario usuario = new Usuario();
        modeloMapper.map(usuarioDtoRegistro, usuario);
        usuario.setActive(false);

        usuarioRepository.save(usuario);

        String uuid = UUID.randomUUID().toString();
        VerificacionUuid verificacionUUID = new VerificacionUuid(uuid, usuario, LocalDateTime.now().plusHours(24));
        verificacionUuidRepository.save(verificacionUUID);

        // Llamar al método para enviar el correo electrónico
        emailUtil.enviarEmailConUUIDParaVerificarEmail(usuario.getNombre(), usuario.getDireccionEmail(), uuid);
        return usuario;
    }

    @Override
    public String verificarCuentaPorDireccionEmailYUuid(String uuid) {
        VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        Usuario usuario = verificacionUuid.getUsuario();
        usuario.setActive(true);
        usuarioRepository.save(usuario);

        return "Cuenta verificada exitosamente.";
        }
    
    //OJO antes de llamar a esta funcion hay que establecer el id al usuario el cual se pasa por parámetro por url
    @Override
    public Usuario actualizar(int idUsuario, UsuarioDtoRegistro usuarioDtoRegistro) {

    	Usuario usuarioAntiguo = usuarioRepository.findById(idUsuario)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe usuario de idUsuario "+idUsuario+":"));
    	
    	if (usuarioAntiguo.getActive()==false) {
    		throw new IllegalStateException("La cuenta de usuario de idUsuario "+idUsuario+" no ha sido verificada, luego no puede actualizarse.");
    	}
    	
    	Optional<Usuario> usuarioConDireccionEmailYaExistente = usuarioRepository.findByDireccionEmail(usuarioDtoRegistro.getDireccionEmail());
    	
    	if (usuarioConDireccionEmailYaExistente.isPresent() && usuarioAntiguo.getIdUsuario()!=usuarioConDireccionEmailYaExistente.get().getIdUsuario()) {
            throw new DataIntegrityViolationException("Ya existe otro usuario con la dirección de correo electrónico " + usuarioDtoRegistro.getDireccionEmail() + ".");
        }
		
		usuarioAntiguo.setNombre(usuarioDtoRegistro.getNombre());
		usuarioAntiguo.setApellido1(usuarioDtoRegistro.getApellido1());
		usuarioAntiguo.setApellido2(usuarioDtoRegistro.getApellido2());
		usuarioAntiguo.setPassword(usuarioDtoRegistro.getPassword());
    	if (!usuarioDtoRegistro.getDireccionEmail().equals(usuarioAntiguo.getDireccionEmail())) {
        	usuarioAntiguo.setDireccionEmail(usuarioDtoRegistro.getDireccionEmail());
        	usuarioAntiguo.setActive(false);
        	
        	VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new EntityNotFoundException("Verificación UUID no encontrada"));

            String nuevoUuid = UUID.randomUUID().toString();
            verificacionUuid.setUuid(nuevoUuid);
            verificacionUuid.setFechaExpiracion(LocalDateTime.now().plusDays(1));
            verificacionUuidRepository.save(verificacionUuid);
            emailUtil.enviarEmailConNuevoUuidParaVerificarEmail(usuarioAntiguo.getNombre(), usuarioAntiguo.getDireccionEmail(), nuevoUuid);
        }
    	return usuarioRepository.save(usuarioAntiguo);
    }
    	
    @Override
    public boolean borrarPorId(int id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Usuario regenerarUuidParaUsuarioNoVerificado(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("No existe usuario de idUsuario "+idUsuario+"."));

        if (!usuario.getActive()) {
            VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new EntityNotFoundException("Verificación UUID no encontrada"));

            String nuevoUuid = UUID.randomUUID().toString();
            verificacionUuid.setUuid(nuevoUuid);
            verificacionUuid.setFechaExpiracion(LocalDateTime.now().plusDays(1)); // Ajusta la fecha de expiración según sea necesario
            verificacionUuidRepository.save(verificacionUuid);

            emailUtil.enviarEmailConNuevoUuidParaVerificarEmail(usuario.getNombre(), usuario.getDireccionEmail(), nuevoUuid);

            return usuario;
        } else {
            throw new IllegalStateException("No se puede regenerar el UUID de un usuario activo.");
        }
    }
    
}