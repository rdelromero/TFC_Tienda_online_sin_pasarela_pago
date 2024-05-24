package com.nombreGrupo.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nombreGrupo.seguridad.FiltroQueExtiendeOncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FiltroQueExtiendeOncePerRequestFilter filtroQueExtiendeOncePerRequestFilter;
    private final AuthenticationProvider authProvider;

    
    public SecurityConfig(FiltroQueExtiendeOncePerRequestFilter filtroQueExtiendeOncePerRequestFilter, AuthenticationProvider authProvider) {
        this.filtroQueExtiendeOncePerRequestFilter = filtroQueExtiendeOncePerRequestFilter;
        this.authProvider = authProvider;
    }
    
    @Bean
    public UserDetailsManager usersCustom(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select username,password,active from Usuarios where username=?");
        users.setAuthoritiesByUsernameQuery("select username, role from Usuarios where username=?");
        return users;
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
		.csrf(csrf -> csrf
			.disable());
		// Los recursos estáticos no requieren autenticación
		http.authorizeHttpRequests(authorize -> authorize
			// Vistas que no quieren autenticación. Supongo que no hace falta añadir "/login"porque no uso el login que viene por defecto
			.requestMatchers("/").permitAll() // Si implementásemos login y signup .requestMatchers("/", "/login", "/signup").permitAll()
			.requestMatchers("/api/**").permitAll()
			.requestMatchers("/static/**", "/css/**", "/js/**", "/imagenes/**").permitAll()
			// Todas las demás URLs de la Aplicación requieren autenticación
			// Asignar permisos a URLs por ROLES
			.requestMatchers("/fabricantes", "/logout").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
			.requestMatchers("/categorias/**", "/subcategorias/**", "/productos/**", "/pedidos/**", "/usuarios/**").hasAnyAuthority("ROLE_ADMIN")
			//Todo lo que está permitido lo permito, el resto no
			.anyRequest().authenticated()
	    )
		// El formulario de Login no requiere autenticacion
		// Al loguearse nos manda a /eventos/destacados
		.formLogin(form -> form
				.defaultSuccessUrl("/", true).permitAll() // Otra opción es que se redigira a productos .defaultSuccessUrl("/productos", true)
				//Formulario permitido, el que trae el propio spring boot security
		)
		//Si tuviésemos nuestro propio login hecho
		//.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/blabla").permitAll());
		//Al pincar en Cerrar sesión volverá al home (de no ponerlo volvería al login)
		.logout(logout -> logout.logoutSuccessUrl("/").permitAll()
		)
		.authenticationProvider(authProvider)
        .addFilterBefore(filtroQueExtiendeOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
    
    /*Filtro original el cual estaba solo pensado para la API*/
    //authenticationProvider y addFilterBefore tiene que ver con la validación del token
    /*@Bean
    public SecurityFilterChain seguridadFiltroCadena(HttpSecurity HtSe) throws Exception
    {
        HtSe
        .csrf(csrf -> csrf
        	.disable()
        )
        .authorizeHttpRequests(authRequest -> authRequest
            .requestMatchers("/", "/signup", "/logout").permitAll()
            .requestMatchers("/api/**").permitAll()
            .requestMatchers("/**").hasRole("ADMIN")
            .anyRequest().authenticated() // Todas las demás solicitudes deben estar autenticadas
        )
        .formLogin(form -> form
    		.defaultSuccessUrl("/", true) // Otra opción .defaultSuccessUrl("/productos", true)
    		//Formulario permitido, el que trae el propio spring boot security
    		.permitAll())
            .logout(logout -> logout
                .permitAll() // Permite el logout para todos
            )
            .sessionManagement(sessionManager-> sessionManager 
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ) // Esto de SessionCreationPolicy.STATELESS está especialmente pensado para la API, si lo ponemos de manera general NO funcionará la autenticación en los html
            .authenticationProvider(authProvider)
            .addFilterBefore(filtroQueExtiendeOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return HtSe.build();
    }*/
    
    //Debe implementarse un bean de AuthenticationProvider y una clase que extienda OncePerRequestFilter

}