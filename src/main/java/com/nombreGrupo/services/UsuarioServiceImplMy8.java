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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.entities.Pedido;
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
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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
        return usuarioRepository.findByEnabledTrue();
    }
    
    @Override
    public Usuario encontrarPorId(int idUsuario) {
        return usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new EntityNotFoundException("No existe un usuario de id " + idUsuario + "."));
    }

    @Override
    public Usuario encontrarPorUsername(String direccionEmail) {
  	  Optional<Usuario> usuario = usuarioRepository.findByUsername(direccionEmail);
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
    	
        if (usuarioRepository.findByUsername(usuarioDtoRegistro.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("No se ha podido crear el usuario. Ya existe un usuario con la dirección de correo electrónico " + usuarioDtoRegistro.getUsername() + ".");
        }
    	
        if (usuarioDtoRegistro.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        
        Usuario usuario = new Usuario();
        usuarioDtoRegistro.setPassword(passwordEncoder.encode(usuarioDtoRegistro.getPassword()));
        modeloMapper.map(usuarioDtoRegistro, usuario);
        usuario.setEnabled(false);
        usuario.setRole(Usuario.Role.ROLE_USER);

        usuarioRepository.save(usuario);

        String uuid = UUID.randomUUID().toString();
        VerificacionUuid verificacionUUID = new VerificacionUuid(uuid, usuario, LocalDateTime.now().plusHours(24));
        verificacionUuidRepository.save(verificacionUUID);

        // Llamar al método para enviar el correo electrónico
        emailUtil.enviarEmailConUUIDParaVerificarEmail(usuario.getNombre(), usuario.getUsername(), uuid);
        return usuario;
    }

    @Override
    public String verificarCuentaPorDireccionEmailYUuid(String uuid) {
        VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        Usuario usuario = verificacionUuid.getUsuario();
        usuario.setEnabled(true);
        usuarioRepository.save(usuario);

        return "Cuenta verificada exitosamente.";
        }
    
    //OJO antes de llamar a esta funcion hay que establecer el id al usuario el cual se pasa por parámetro por url
    @Override
    public Usuario actualizar(int idUsuario, UsuarioDtoRegistro usuarioDtoRegistro) {

    	Usuario usuarioAntiguo = usuarioRepository.findById(idUsuario)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe usuario de idUsuario "+idUsuario+":"));
    	
    	if (usuarioAntiguo.isEnabled()==false) {
    		throw new IllegalStateException("La cuenta de usuario de idUsuario "+idUsuario+" no ha sido verificada, luego no puede actualizarse.");
    	}
    	
    	Optional<Usuario> usuarioConDireccionEmailYaExistente = usuarioRepository.findByUsername(usuarioDtoRegistro.getUsername());
    	
    	if (usuarioConDireccionEmailYaExistente.isPresent() && usuarioAntiguo.getIdUsuario()!=usuarioConDireccionEmailYaExistente.get().getIdUsuario()) {
            throw new DataIntegrityViolationException("Ya existe otro usuario con la dirección de correo electrónico " + usuarioDtoRegistro.getUsername() + ".");
        }
		
		usuarioAntiguo.setNombre(usuarioDtoRegistro.getNombre());
		usuarioAntiguo.setApellido1(usuarioDtoRegistro.getApellido1());
		usuarioAntiguo.setApellido2(usuarioDtoRegistro.getApellido2());
		usuarioAntiguo.setPassword(passwordEncoder.encode(usuarioDtoRegistro.getPassword()));
		
    	if (!usuarioDtoRegistro.getUsername().equals(usuarioAntiguo.getUsername())) {
        	usuarioAntiguo.setUsername(usuarioDtoRegistro.getUsername());
        	usuarioAntiguo.setEnabled(false);
        	
        	VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new EntityNotFoundException("Verificación UUID no encontrada"));

            String nuevoUuid = UUID.randomUUID().toString();
            verificacionUuid.setUuid(nuevoUuid);
            verificacionUuid.setFechaExpiracion(LocalDateTime.now().plusDays(1));
            verificacionUuidRepository.save(verificacionUuid);
            emailUtil.enviarEmailConNuevoUuidParaVerificarEmail(usuarioAntiguo.getNombre(), usuarioAntiguo.getUsername(), nuevoUuid);
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

        if (!usuario.isEnabled()) {
            VerificacionUuid verificacionUuid = verificacionUuidRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new EntityNotFoundException("Verificación UUID no encontrada"));

            String nuevoUuid = UUID.randomUUID().toString();
            verificacionUuid.setUuid(nuevoUuid);
            verificacionUuid.setFechaExpiracion(LocalDateTime.now().plusDays(1)); // Ajusta la fecha de expiración según sea necesario
            verificacionUuidRepository.save(verificacionUuid);

            emailUtil.enviarEmailConNuevoUuidParaVerificarEmail(usuario.getNombre(), usuario.getUsername(), nuevoUuid);

            return usuario;
        } else {
            throw new IllegalStateException("No se puede regenerar el UUID de un usuario activo.");
        }
    }
    
    @Override
    public Usuario crearAdmin(UsuarioDtoRegistro usuarioDto) {
        if (usuarioRepository.findByUsername(usuarioDto.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Ya existe un usuario con la dirección de correo electrónico " + usuarioDto.getUsername() + ".");
        }

        if (usuarioDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        Usuario usuario = new Usuario();
        usuarioDto.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        modeloMapper.map(usuarioDto, usuario);
        usuario.setEnabled(true); // Activa la cuenta directamente
        usuario.setRole(Usuario.Role.ROLE_ADMIN);

        return usuarioRepository.save(usuario);
    }
}