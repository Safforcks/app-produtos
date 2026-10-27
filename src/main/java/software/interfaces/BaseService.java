package software.interfaces;

import java.util.List;

public interface BaseService<T> {

    void cadastrar(T entidade);

    List<T> consultar();
}
