package com.example.finanzas.config;

import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.CategoriaOrigen;
import com.example.finanzas.entity.CategoriaTipo;
import com.example.finanzas.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SystemCategorySeeder implements ApplicationRunner {
    private final CategoriaRepository categoriaRepository;

    public SystemCategorySeeder(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean hasSystemCategories = categoriaRepository.findAll().stream()
                .anyMatch(c -> c.getUsuarioId() == null && Boolean.TRUE.equals(c.getActiva()));
        if (hasSystemCategories) {
            return;
        }

        List<Categoria> defaults = List.of(
                system("Salario", CategoriaTipo.INGRESO, "wallet"),
                system("Ventas", CategoriaTipo.INGRESO, "shopping-bag"),
                system("Alimentación", CategoriaTipo.EGRESO, "utensils"),
                system("Transporte", CategoriaTipo.EGRESO, "bus"),
                system("Vivienda", CategoriaTipo.EGRESO, "home"),
                system("Servicios", CategoriaTipo.EGRESO, "receipt"),
                system("Salud", CategoriaTipo.EGRESO, "heart"),
                system("Educación", CategoriaTipo.EGRESO, "book"),
                system("Otros", CategoriaTipo.AMBOS, "more-horizontal"));
        categoriaRepository.saveAll(defaults);
    }

    private Categoria system(String nombre, CategoriaTipo tipo, String icono) {
        Categoria categoria = new Categoria();
        categoria.setUsuarioId(null);
        categoria.setNombre(nombre);
        categoria.setTipo(tipo);
        categoria.setOrigen(CategoriaOrigen.SISTEMA);
        categoria.setIcono(icono);
        categoria.setActiva(true);
        return categoria;
    }
}
