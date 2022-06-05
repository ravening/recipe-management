package com.ravening.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IngredientsDto {
    @JsonProperty("ingredient_id")
    private long ingredientId;

    @NotBlank
    @JsonProperty("ingredientName")
    private String ingredientName;

    @JsonProperty("quantity")
    private String quantity;

}
