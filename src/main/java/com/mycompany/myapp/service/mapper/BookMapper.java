package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.Category;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.CategoryDTO;
import com.mycompany.myapp.service.dto.PublisherDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "categories", source = "categories", qualifiedByName = "categoryTitleSet")
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherName")
    BookDTO toDto(Book s);

    @Mapping(target = "removeCategory", ignore = true)
    Book toEntity(BookDTO bookDTO);

    @Named("categoryTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    CategoryDTO toDtoCategoryTitle(Category category);

    @Named("categoryTitleSet")
    default Set<CategoryDTO> toDtoCategoryTitleSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryTitle).collect(Collectors.toSet());
    }

    @Named("publisherName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PublisherDTO toDtoPublisherName(Publisher publisher);
}
