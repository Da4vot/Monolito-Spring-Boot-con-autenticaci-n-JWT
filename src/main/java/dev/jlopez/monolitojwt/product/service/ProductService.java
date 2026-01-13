package dev.jlopez.monolitojwt.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jlopez.monolitojwt.exception.BadRequestException;
import dev.jlopez.monolitojwt.exception.ProductNotFoundException;
import dev.jlopez.monolitojwt.product.dto.requestDTO.ProductRequestDTO;
import dev.jlopez.monolitojwt.product.dto.responseDTO.ProductResponseDTO;
import dev.jlopez.monolitojwt.product.model.Product;
import dev.jlopez.monolitojwt.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    //create
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO){
        if(productRepository.existsByName(requestDTO.name())){
            throw new BadRequestException("El producto " + requestDTO.name() + " ya existe."
            );
        }
        Product product = mapToEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }

    // obtener por id
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Integer id){
        return productRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado con Id: "+ id));
    }

    //obtener todos
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    //Actualizar
    @Transactional
    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO productDTO){
        Product existingProduct = productRepository.findById(id)
                            .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado con Id: "+ id));
        existingProduct.setName(productDTO.name());
        existingProduct.setDescription(productDTO.description());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setStock(productDTO.stock());

        Product update = productRepository.save(existingProduct);
        return mapToDTO(update);
    }

    //eliminar
    @Transactional
    public void deleteProduct(Integer id){
        Product existingProduct = productRepository.findById(id)
                            .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado con Id: "+ id));
        productRepository.delete(existingProduct);
    }
    
    //mapear de entidad a dto
    private ProductResponseDTO mapToDTO(Product product){
        return  new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
    }
    //convertir RequestDto a entidad
    private Product mapToEntity(ProductRequestDTO requestDTO){
        return Product.builder()
                    .name(requestDTO.name())
                    .description(requestDTO.description())
                    .price(requestDTO.price())
                    .stock(requestDTO.stock())
                    .build();

    }

}
