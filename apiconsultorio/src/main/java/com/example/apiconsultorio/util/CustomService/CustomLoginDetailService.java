package com.example.apiconsultorio.util.CustomService;

import com.example.apiconsultorio.dao.LoginRepository;
import com.example.apiconsultorio.dao.UsuarioRepository;
import com.example.apiconsultorio.model.Login;
import com.example.apiconsultorio.model.Usuario;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomLoginDetailService implements UserDetailsService {

    private final LoginRepository loginRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomLoginDetailService(LoginRepository loginRepository, UsuarioRepository usuarioRepository) {
        this.loginRepository = loginRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Login login = Optional.ofNullable(loginRepository.findByUsername(username))
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado!"));

        List<GrantedAuthority> authorityListAdmin = AuthorityUtils.createAuthorityList("ROLE_administrador", "ROLE_auxiliar", "ROLE_profissional");
        List<GrantedAuthority> authorityListAux = AuthorityUtils.createAuthorityList("ROLE_auxiliar");
        List<GrantedAuthority> authorityListProf = AuthorityUtils.createAuthorityList("ROLE_profissional");

        int usuario_id = login.getUsuarioId();
        Usuario usuario = usuarioRepository.findById(usuario_id);
        String categ = usuario.getCateg();
        List<GrantedAuthority> select = null;
        if(categ.equals("administrador")){
            select = authorityListAdmin;
        }else if(categ.equals("auxiliar")){
            select = authorityListAux;
        } else if(categ.equals("profissional")){
            select = authorityListProf;
        } else{
            throw new ValidateAtributesException("Você não tem permissão para realizar a operação!");
        }

        return new org.springframework.security.core.userdetails.User(login.getUsername(), login.getPassword(), select);
    }

}
