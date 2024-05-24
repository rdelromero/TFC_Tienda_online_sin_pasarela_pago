package com.nombreGrupo.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.nombreGrupo.services.UsuarioService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

	private static final String CLAVE_SECRETA = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";
	
    /*  Key es una interfaz de la biblioteca java.security que representa una clave de criptografía. Esta clave se utiliza para firmar o verificar tokens JWT 
     * (JSON Web Tokens) en el proceso de creación y validación de estos tokens.
     */
	
    public Key obtenerKey() {
        byte[] keyBytes = Decoders.BASE64.decode(CLAVE_SECRETA);
        return Keys.hmacShaKeyFor(keyBytes);
    }
	
    /* CREACIÓN DEL JWT ----------------------------------------------------------------------*/
    
    /* Map<String, Object> es una interfaz genérica en Java que representa un mapa o diccionario, donde las claves son de tipo String y los valores son de 
     * tipo Object. En términos más simples, es una estructura de datos que permite almacenar pares clave-valor, 
     * donde cada clave es una cadena de texto y cada valor puede ser de cualquier tipo de objeto ((o sea el valor puede ser de cualquier clase)
     */
    
    /* Jwts es una clase de la biblioteca io.jsonwebtoken que proporciona varias utilidades para crear, analizar, firmar y validar tokens JWT 
     * (JSON Web Tokens).
     */
    
    /* Un JWT (JSON Web Token) se compone de:
     * 1 Encabezado (header). 
     * 2 Paylaod (claims). Existen tres tipos de claims: registrados, públicos y privados.
     * 3 Firma (signature)
     */
    
    /* Generalmente consta del tipo de token (JWT) y el algoritmo de firma que se utiliza (por ejemplo, HMAC SHA256).
     */
    
    /* Payload
     * El payload es la parte que contiene los claims. Los claims son pares clave-valor y pueden incluir información sobre el usuario y otros datos 
     * relevantes. p. ej. "nombre": "Fernando Alonso"
     * Existen tres tipos de claims: registrados, públicos y privados.
     * Los claims registrados utilizados aquí son "subject", "issued at" y expiration.
     * - subject es utilizado para identificar al sujeto del token, que generalmente es el nombre de usuario.
     * - issued at registra la fecha y hora en que se emitió el token.
     * - expiration define la fecha y hora en que expira el token
     * Los claims privados se utilizan para incluir información personalizada que solo es relevante para las partes involucradas en la comunicación
     */
    
    /* Firma
     * La firma se utiliza para verificar que el mensaje no ha sido modificado y, en algunos casos, para verificar la identidad del remitente.
     * La firma se crea codificando en base64url el encabezado y el payload, concatenándolos con un punto (.), y luego firmándolos usando un algoritmo 
     * especificado en el encabezado.
     */
    
    /* Claims es una interfaz que representa el conjunto de declaraciones contenidas en el payload de un token JWT.
    */
    
    // Función llamada al crear usuario o loguear usuario
    // prepara los claims necesarios y delega la tarea de crear el token al método crearToken.
    public String crearJWTConMapaVacioDeClaims(UserDetails userDetails) {
        return crearJWT(new HashMap<>(), userDetails);
    }
    
    private String crearJWT(Map<String, Object> claimsAdicionales, UserDetails usuarioDetalles) {
        return Jwts.builder()
                .setClaims(claimsAdicionales)
                .setSubject(usuarioDetalles.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //24 minutos
                .signWith(obtenerKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /* VALIDACIÓN DEL TOKEN ----------------------------------------------------------------------*/
    
    /* Function<T, R> es una interfaz funcional genérica en Java que forma parte del paquete java.util.function. Está diseñada para representar una función 
     * que acepta un argumento de tipo T y produce un resultado de tipo R.
     */
    
    //Función llamada por la función doFilterInternal
    public String extraerUsername(String token) {
        return extraerValorDeClaim(token, Claims::getSubject);
    }

    public <T> T extraerValorDeClaim(String token, Function<Claims, T> funcionExtraerValorDeUnClaim) {
        final Claims reclamos = extraerTodosClaims(token);
        return funcionExtraerValorDeUnClaim.apply(reclamos);
    }
    
    private Claims extraerTodosClaims(String token) {
        return Jwts
                .parserBuilder() // Inicia el proceso de parsing (análisis) del token JWT utilizando el parser de Jwts:
                .setSigningKey(obtenerKey()) // Establece la clave de firma que se utilizará para validar el token:
                .build() // Construye el parser para que esté listo para parsear el token:
                .parseClaimsJws(token) // Parsea el token y extrae los claims:
                .getBody();
    }
    
    //Función llamada por la función doFilterInternal
    public Boolean validarToken(String token, UserDetails detallesUsuario) {
        final String nombreUsuario = extraerUsername(token);
        return (nombreUsuario.equals(detallesUsuario.getUsername()) && !esTokenExpirado(token));
    }
    
    private Boolean esTokenExpirado(String token) {
        return extraerFechaExpiracion(token).before(new Date());
    }
    
    public Date extraerFechaExpiracion(String token) {
        return extraerValorDeClaim(token, Claims::getExpiration);
    }
    
    @Autowired
    private UsuarioService usuarioService;
    
    //Extraer el idUsuario de un jwt
    public int extraerIdUsuario(String token) {
    	String username = extraerUsername(token);
        return usuarioService.encontrarPorUsername(username).getIdUsuario();
    }

}