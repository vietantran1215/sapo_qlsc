package com.sapo.qlsc.service;

import com.sapo.qlsc.dto.ProductDTO;
import com.sapo.qlsc.entity.Product;
import com.sapo.qlsc.exception.NotANumberException;
import com.sapo.qlsc.exception.productException.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ProductService {
    public Page<ProductDTO> getAll(String search, Pageable pageable);

    public Page<ProductDTO> getAllAccessories(String search, Pageable pageable);

    public Page<ProductDTO> getAllServices(String search, Pageable pageable);

    public ProductDTO save(Product product) throws Exception;

    public String createNewCode() throws NotANumberException;

    public byte[] getImageByte(HttpServletResponse response, String imageName) throws IOException;

    public ProductDTO getOneById(Long id) throws ProductNotFoundException;

    public void deleteById(Long id) throws ProductNotFoundException;

    public boolean isCodeExist(String code);

    public boolean isCodeExistToUpdate(String code, Long id);

    public ProductDTO getOneByIdAndType(Long id, Byte type) throws ProductNotFoundException;

    public boolean isNameExist(String name);

    public boolean isNameExistToUpdate(String name, Long id);

    public void multiDelete(Long[] idArray);
}
