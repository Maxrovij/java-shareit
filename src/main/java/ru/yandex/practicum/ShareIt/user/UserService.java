package ru.yandex.practicum.ShareIt.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.exceptions.DataAlreadyExistsException;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createNew(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
            throw new IncorrectDataException("Missing email!");
        if (!userDto.getEmail().contains("@"))
            throw new IncorrectDataException("Invalid email!");
        if (userDto.getName() == null || userDto.getName().isEmpty())
            throw new IncorrectDataException("Invalid name");

        User user = new User(userDto.getName(), userDto.getEmail());
        try {
            return UserMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DataAlreadyExistsException("This email is already used!");
        }

    }

    public UserDto patch(UserDto userDto) {
        Optional<User> maybeUser = userRepository.findById(userDto.getId());
        if (maybeUser.isEmpty())
            throw new DataNotFoundException(String.format("User with id %d not found!", userDto.getId()));

        User userToEdit = maybeUser.get();
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            userToEdit.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!userDto.getEmail().contains("@")) throw new IncorrectDataException("Invalid email!");
            userToEdit.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.toDto(userRepository.save(userToEdit));
        } catch (DataIntegrityViolationException e) {
            throw new DataAlreadyExistsException("This email is already used!");
        }
    }

    public UserDto getById(Long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) return UserMapper.toDto(maybeUser.get());
        throw new DataNotFoundException(String.format("User with id %d not found!", id));
    }

    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        maybeUser.ifPresent(userRepository::delete);
    }
}
