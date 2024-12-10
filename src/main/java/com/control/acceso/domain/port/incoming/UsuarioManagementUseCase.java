package com.control.acceso.domain.port.incoming;

import com.control.acceso.domain.model.Usuario;
import java.util.List;

public interface UsuarioManagementUseCase {
    Usuario obtenerUsuarioPorEmail(String email);
    Usuario obtenerUsuarioPorId(Long id);
    List<Usuario> obtenerTodosLosUsuarios();
    Usuario crearUsuario(Usuario usuario);
    Usuario actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    boolean existeUsuarioPorEmail(String email);
}
