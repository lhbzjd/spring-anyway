package ink.anyway.component.web.config;

import ink.anyway.component.common.plugin.CustomConfigPlugin;
import ink.anyway.component.web.encoder.Md5PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomConfigPlugin customConfigPlugin;

    @Autowired
	private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(new Md5PasswordEncoder());
        provider.setUserDetailsService(this.userDetailsService);

        auth.authenticationProvider(provider);

//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
           .formLogin()
//           .loginPage("/login")
//           .loginProcessingUrl("/login")
//           .defaultSuccessUrl("/homepage")
           .usernameParameter("username")
           .passwordParameter("password")
//           .failureUrl("/login?error=true")
           .permitAll()
           .and()
           .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//           .logoutSuccessUrl("/login")
           .invalidateHttpSession(true)
           .permitAll()
           .and()
           .csrf().disable().authorizeRequests()
           .antMatchers(customConfigPlugin.getSecurityAntMatchers()).permitAll()
           .anyRequest().authenticated();

    }


}
