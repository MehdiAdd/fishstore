package com.commerce.backend.service;

import com.commerce.backend.converter.product.ProductDetailsResponseConverter;
import com.commerce.backend.converter.product.ProductResponseConverter;
import com.commerce.backend.converter.product.ProductVariantResponseConverter;
import com.commerce.backend.converter.product.ProductsResponseConverter;
import com.commerce.backend.dao.*;
import com.commerce.backend.error.exception.InvalidArgumentException;
import com.commerce.backend.error.exception.ResourceNotFoundException;
import com.commerce.backend.model.dto.AddItemDto;
import com.commerce.backend.model.entity.*;
import com.commerce.backend.model.response.product.ProductDetailsResponse;
import com.commerce.backend.model.response.product.ProductResponse;
import com.commerce.backend.model.response.product.ProductVariantResponse;
import com.commerce.backend.model.response.product.ProductsResponse;
import com.commerce.backend.model.specs.ProductVariantSpecs;
import com.commerce.backend.service.cache.ProductCacheService;
import com.commerce.backend.service.cache.ProductVariantCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductCacheService productCacheService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    private final  CartRepository cartRepository;

    private final OrderRepository orderRepository;
    private final ProductVariantCacheService productVariantCacheService;
    private final ProductResponseConverter productResponseConverter;
    private final ProductVariantResponseConverter productVariantResponseConverter;
    private final ProductDetailsResponseConverter productDetailsResponseConverter;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductsResponseConverter productsResponseConverter;
    @Autowired
    public ProductServiceImpl(ProductCacheService productCacheService,
                              ProductRepository productRepository,
                              ProductVariantRepository productVariantRepository,
                              CartRepository cartRepository, OrderRepository orderRepository, ProductVariantCacheService productVariantCacheService,
                              ProductResponseConverter productResponseConverter,
                              ProductVariantResponseConverter productVariantResponseConverter,
                              ProductDetailsResponseConverter productDetailsResponseConverter, ProductCategoryRepository productCategoryRepository,
                              ProductsResponseConverter productsResponseConverter) {
        this.productCacheService = productCacheService;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productVariantCacheService = productVariantCacheService;
        this.productResponseConverter = productResponseConverter;
        this.productVariantResponseConverter = productVariantResponseConverter;
        this.productDetailsResponseConverter = productDetailsResponseConverter;
        this.productCategoryRepository = productCategoryRepository;
        this.productsResponseConverter=productsResponseConverter;
    }

    @Override
    public ProductDetailsResponse findByUrl(String url) {
        Product product = productCacheService.findByUrl(url);
        if (Objects.isNull(product)) {
            throw new ResourceNotFoundException(String.format("Product not found with the url %s", url));
        }
        return productDetailsResponseConverter.apply(product);
    }


    @Override
    public ProductVariant findProductVariantById(Long id) {
        ProductVariant productVariant = productVariantCacheService.findById(id);
        if (Objects.isNull(productVariant)) {
            throw new ResourceNotFoundException(String.format("Could not find any product variant with the id %d", id));
        }
        return productVariant;
    }

    @Override
    public List<ProductVariantResponse> getAll(Integer page, Integer size, String sort, String category, Float minPrice, Float maxPrice, String color) {
        PageRequest pageRequest;
        if (Objects.nonNull(sort) && !sort.isBlank()) {
            Sort sortRequest = getSort(sort);
            if (Objects.isNull(sortRequest)) {
                throw new InvalidArgumentException("Invalid sort parameter");
            }
            pageRequest = PageRequest.of(page, size, sortRequest);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        Specification<ProductVariant> combinations =
                Objects.requireNonNull(Specification.where(ProductVariantSpecs.withColor(color)))
                        .and(ProductVariantSpecs.withCategory(category))
                        .and(ProductVariantSpecs.minPrice(minPrice))
                        .and(ProductVariantSpecs.maxPrice(maxPrice));

        return productVariantRepository.findAll(combinations, pageRequest)
                .stream()
                .map(productVariantResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public ProductsResponse getListProduct() {

        return productsResponseConverter.apply(productVariantRepository.findAll());
    }

    @Override
    public ProductsResponse incrementCartItem(Long cartItemId, Integer amount) {
        Optional<ProductVariant> productVariantOptional=productVariantRepository.findById(cartItemId);
        if(productVariantOptional.isPresent()){
            ProductVariant productVariant=productVariantOptional.get();
            productVariant.setStock(productVariant.getStock()+amount);
            productVariantRepository.save(productVariant);
        }else{
            throw new ResourceNotFoundException("Un erreur s'est produit");
        }
        return getListProduct();


    }

    @Override
    public ProductsResponse decrementCartItem(Long cartItemId, Integer amount) {
        Optional<ProductVariant> productVariantOptional=productVariantRepository.findById(cartItemId);
        if(productVariantOptional.isPresent()){
            ProductVariant productVariant=productVariantOptional.get();
            productVariant.setStock(productVariant.getStock()-amount);
            productVariantRepository.save(productVariant);
        }else{
            throw new ResourceNotFoundException("Un erreur s'est produit");
        }
        return getListProduct();
    }

    @Override
    @Transactional
    public ProductsResponse removeFromCart(Long cartItemId) {
        Optional<ProductVariant> productVariantOptional=productVariantRepository.findById(cartItemId);
        if(productVariantOptional.isPresent()){
            ProductVariant productVariant=productVariantOptional.get();
            Long productId=productVariant.getProduct().getId();

            Optional<Product> productOptionalp=productRepository.findById(productId);
            if(productOptionalp.isPresent()){
                Product p=productOptionalp.get();
//                List<ProductVariant> list=p.getProductVariantList().stream().filter(pv->pv.getId()!=productVariant.getId()).collect(Collectors.toList());
//                p.getProductVariantList().clear();
//                p.getProductVariantList().addAll(list);
                p.getProductVariantList().remove(productVariant);
                productRepository.save(p);

                productVariant.setProduct(null);
                productVariantRepository.save(productVariant);

                //productRepository.delete(productOptionalp.get());


                orderRepository.deleteByOrderDetailList_ProductVariant_Id(productVariant.getId());
                cartRepository.deleteByCartItemList_ProductVariant_Id(productVariant.getId());
                productVariantRepository.delete(productVariant);

                if(p!=null && p.getProductVariantList().size()==0){

                        productRepository.delete(p);
                    }
                }


        }else{
            throw new ResourceNotFoundException("Un erreur s'est produit");
        }
        return getListProduct();
    }

    @Override
    public Long getAllCount(String category, Float minPrice, Float maxPrice, String color) {
        Specification<ProductVariant> combinations =
                Objects.requireNonNull(Specification.where(ProductVariantSpecs.withColor(color)))
                        .and(ProductVariantSpecs.withCategory(category))
                        .and(ProductVariantSpecs.minPrice(minPrice))
                        .and(ProductVariantSpecs.maxPrice(maxPrice));

        return productVariantRepository.count(combinations);
    }

    @Override
    public List<ProductResponse> getRelatedProducts(String url) {
        Product product = productCacheService.findByUrl(url);
        if (Objects.isNull(product)) {
            throw new ResourceNotFoundException("Related products not found");
        }
        List<Product> products = productCacheService.getRelatedProducts(product.getProductCategory(), product.getId());
        return products
                .stream()
                .map(productResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getNewlyAddedProducts() {
        List<Product> products = productCacheService.findTop8ByOrderByDateCreatedDesc();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Newly added products not found");
        }
        return products
                .stream()
                .map(productResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariantResponse> getMostSelling() {
        List<ProductVariant> productVariants = productVariantCacheService.findTop8ByOrderBySellCountDesc();
        if (productVariants.isEmpty()) {
            throw new ResourceNotFoundException("Most selling products not found");
        }

        return productVariants
                .stream()
                .map(productVariantResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getInterested() {
        List<Product> products = productCacheService.findTop8ByOrderByDateCreatedDesc();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Interested products not found");
        }
        return products
                .stream()
                .map(productResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProductDisplay(String keyword, Integer page, Integer size) {
        if (Objects.isNull(page) || Objects.isNull(size)) {
            throw new InvalidArgumentException("Page and size are required");
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Product> products = productRepository.findAllByNameContainingIgnoreCase(keyword, pageRequest);
        return products
                .stream()
                .map(productResponseConverter)
                .collect(Collectors.toList());
    }

    @Override
    public void addItem(AddItemDto addItemDto) {


        ProductVariant productVariant=new ProductVariant();

        productVariant.setDate(LocalDateTime.now());
        productVariant.setImage(addItemDto.getImage());
        productVariant.setThumb(addItemDto.getThumb());
        productVariant.setLive(1);
        productVariant.setCargoPrice(addItemDto.getCargo_price().floatValue());
        productVariant.setPrice(addItemDto.getPrice().floatValue());
        productVariant.setTaxPercent(addItemDto.getTax_percent().floatValue());
        productVariant.setSellCount(0);
        productVariant.setStock(addItemDto.getStock().intValue());

        Optional<Product> productOp=productRepository.findByUrl(addItemDto.getName());
        if(productOp.isPresent()){
            Product product=productOp.get();
            productVariant.setProduct(product);
            productVariantRepository.save(productVariant);
        }else{
            ProductCategory productCategory  =productCategoryRepository.findByName(addItemDto.getCategory());
            if(productCategory==null){
                productCategory=new ProductCategory();
                productCategory.setName(addItemDto.getCategory());
                productCategory=productCategoryRepository.save(productCategory);
            }
            Product product=new Product();
            product.setName(addItemDto.getName());
           // product.setDateCreated(new Date());
           // product.setLastUpdated(new Date());
            product.setUrl(addItemDto.getName());
            product.setLongDesc(addItemDto.getDescription());
            product.setProductCategory(productCategory);
            product.setSku("000-001");
            product.setUnlimited(1);
            //product.setProductVariantList(Arrays.asList(productVariant));
            try{
                product=productRepository.save(product);
                productVariant.setProduct(product);
                productVariantRepository.save(productVariant);
            }catch (DataIntegrityViolationException e){
                System.out.println(e.fillInStackTrace());
                throw e;
            }

        }

    }


    private Sort getSort(String sort) {
        switch (sort) {
            case "lowest":
                return Sort.by(Sort.Direction.ASC, "price");
            case "highest":
                return Sort.by(Sort.Direction.DESC, "price");
            default:
                return null;
        }
    }

}
