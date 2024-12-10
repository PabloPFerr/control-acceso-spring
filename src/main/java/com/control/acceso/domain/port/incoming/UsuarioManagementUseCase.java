package com.control.acceso.domain.port.incoming;

import com.control.acceso.domain.model.Usuario;
import java.util.List;

public interface UsuarioManagementUseCase {
    Usuario obtenerUsuarioPorEmail(String email);
    boolean existeUsuarioPorEmail(String email);
    Usuario crearUsuario(Usuario usuario);
    Usuario actualizarUsuario(Usuario usuario);
    Usuario obtenerUsuarioPorId(Long id);
    List<Usuario> obtenerTodosLosUsuarios();
    void eliminarUsuario(Long id);
}
