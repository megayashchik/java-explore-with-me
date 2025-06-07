package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
	Long id;

	@NotBlank(message = "Электронная почта не может быть пустой")
	@Email(message = "Адрес электронной почты должен быть в формате user@domain.com")
	@Size(min = 6, max = 254)
	String email;

	@NotBlank
	@Size(min = 2, max = 250)
	String name;
}