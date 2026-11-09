package com.example.demo.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Department;
import com.example.demo.model.Producto;
import com.example.demo.repo.categoriaRepo;
import com.example.demo.repo.productoRepo;

@RestController
public class CategoriaController {

    @Autowired
    private categoriaRepo repo;

    @Autowired
    private productoRepo repoProducto;

    @GetMapping("/categorias")
    public List<Department> findAllcategoria() {
        return repo.findAll();
    }

    @PostMapping("/categoria")
    public Department addcategoria(@RequestBody Department categoria) {
        return repo.save(categoria);
    }

    @Transactional
    @PostMapping("/addProductoCategoria/{productId}/{categoriaId}")
    public Department addProductoCategoria(
            @PathVariable String productId,
            @PathVariable String categoriaId) {

        Producto producto = repoProducto.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

        Department categoria = repo.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        categoria.getProductos().add(producto);

        return repo.save(categoria);
    }

    @Transactional(readOnly = true)
    @GetMapping("/categoria/producto/{productId}")
    public Set<Department> getCategoriasByProducto(@PathVariable String productId) {

        Producto producto = repoProducto.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

        return producto.getDeparments();
    }

    @GetMapping("/categoria/{id}")
    public Department getCategoria(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    @PutMapping("/categoria/{id}")
    public Department updateCategoria(
            @PathVariable String id,
            @RequestBody Department categoria) {

        Department existente = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        return repo.save(Department.builder()
                .id(existente.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .tipo(categoria.getTipo())
                .icono(categoria.getIcono())
                .imagenNavbar(categoria.getImagenNavbar())
                .productos(existente.getProductos())
                .build());
    }

    @DeleteMapping("/categoria/{id}")
    public void deleteCategoria(@PathVariable String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Categoría no encontrada");
        }

        repo.deleteById(id);
    }
}
