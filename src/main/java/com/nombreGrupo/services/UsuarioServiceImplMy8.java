package com.nombreGrupo.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;
import com.nombreGrupo.repositories.UsuarioRepository;
import com.nombreGrupo.util.EmailUtil;
import com.nombreGrupo.util.OtpUtil;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;


@Service
public class UsuarioServiceImplMy8 implements UsuarioService{

	@Autowired
	private OtpUtil otpUtil;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> encontrarTodos() {
        return usuarioRepository.findAll();
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
  	  Optional<Usuario> usuarioOpt = usuarioRepository.findByDireccionEmail(direccionEmail);
        if (!usuarioOpt.isPresent()) {
         	throw new RuntimeException("No existe un usuario de dirección email "+direccionEmail+".");
          }
          return usuarioOpt.get();  // Devuelve el usuario encontrado
    }
    
    @Override
    public Usuario crearYGuardar(UsuarioDtoRegistro usuarioDtoRegistro) {
    	
        if (usuarioRepository.findByDireccionEmail(usuarioDtoRegistro.getDireccionEmail()).isPresent()) {
            throw new DataIntegrityViolationException("No se ha podido crear el usuario. Ya existe un usuario con la dirección de correo electrónico " + usuarioDtoRegistro.getDireccionEmail() + ".");
        }
    	
        if (usuarioDtoRegistro.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        
        String otp = otpUtil.generarOtp();
        try {
        	//Envío del email
            emailUtil.enviarEmailConOtp(usuarioDtoRegistro.getNombre(), usuarioDtoRegistro.getDireccionEmail(), otp);
        }   catch (MessagingException e) {
            throw new IllegalArgumentException("No has introducido una dirección de correo electrónico válida.");
        }
    	Usuario usuario = new Usuario();
    	usuario.setDireccionEmail(usuarioDtoRegistro.getDireccionEmail());
    	usuario.setNombre(usuarioDtoRegistro.getNombre());
    	usuario.setApellido1(usuarioDtoRegistro.getApellido1());
    	usuario.setApellido2(usuarioDtoRegistro.getApellido2());
    	usuario.setPassword(usuarioDtoRegistro.getPassword());
    	usuario.setOtp(otp);
    	usuario.setFechaGeneracionOtp(LocalDateTime.now());
    	return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario verificarCuentaPorDireccionEmailYOpt(String direccionEmail, String otp) {

    	Usuario usuario = usuarioRepository.findByDireccionEmail(direccionEmail)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe usuario de direccionEmail "+direccionEmail+":"));
        
    	// Verificar si la cuenta de usuario ya está verifiacda
        if (usuario.getActive()) {
            throw new IllegalStateException("La cuenta de usuario ya está verificada.");
        }
        // Verificar si el OTP es correcto
        if (!usuario.getOtp().equals(otp)) {
            throw new IllegalArgumentException("El otp proporcionado no es correcto.");
        }
        // Verificar la expiración del OTP
        if (Duration.between(usuario.getFechaGeneracionOtp(), LocalDateTime.now()).toMinutes() > 15) {
            throw new IllegalArgumentException("El otp ha expirado.");
        }
        // Otras operaciones, como activar la cuenta
        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }
    
    @Override
    public Usuario regenerarOtpParaUsuarioNoVerificado(int idUsuario) {
    	Usuario usuario = usuarioRepository.findById(idUsuario)
    	        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario));
    	
    	  if (!usuario.getActive()) {
    		  String otp = otpUtil.generarOtp();
    		  usuario.setOtp(otp);
    		  try {
    		    emailUtil.enviarEmailConOtp(usuario.getNombre(), usuario.getDireccionEmail(), otp);
    		    return usuarioRepository.save(usuario);
    		  } catch (MessagingException e) {
    		    throw new IllegalArgumentException("No has introducido una dirección de email válida.");
    		  }
    	  } else {
    		  throw new IllegalStateException("El usuario de dirección "+usuario.getDireccionEmail()+" ya está verificado.");
    	  }
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
        	usuarioAntiguo.setOtp(otpUtil.generarOtp());
      	    usuarioAntiguo.setFechaGeneracionOtp(LocalDateTime.now());
  		  try {
  		    emailUtil.enviarEmailConOtp(usuarioAntiguo.getNombre(), usuarioAntiguo.getDireccionEmail(), usuarioAntiguo.getOtp());
  		    return usuarioRepository.save(usuarioAntiguo);
  		  } catch (MessagingException e) {
  		    throw new RuntimeException("No has introducido una dirección de email válida.");
  		  }
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
    
    public String loguearse(UsuarioDtoLogin loginDto) {
        Usuario usuario = usuarioRepository.findByDireccionEmail(loginDto.getDireccionEmail())
            .orElseThrow(() -> new EntityNotFoundException("No hay ningún usuario con esa dirección de email."));
        if (!usuario.getActive()) {
            throw new IllegalStateException("La cuenta no está verificada.");
        }
        if (!loginDto.getPassword().equals(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña es incorrecta.");
        }
        return "Logueo exitoso.";
    }
}