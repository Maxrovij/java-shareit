package ru.practicum.shareit.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;

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
        User user = new User(userDto.getName(), userDto.getEmail());
        try {
            return toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DataAlreadyExistsException("This email is already used!");
        }

    }

    @Transactional
    public UserDto patch(UserDto userDto) {
        Optional<User> maybeUser = userRepository.findById(userDto.getId());
        if (maybeUser.isEmpty())
            throw new DataNotFoundException(String.format("User with id %d not found!", userDto.getId()));

        User userToEdit = maybeUser.get();
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            userToEdit.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            userToEdit.setEmail(userDto.getEmail());
        }
        try {
            userRepository.patchUser(userToEdit.getName(), userToEdit.getEmail(), userToEdit.getId());
            return toDto(userToEdit);
        } catch (DataIntegrityViolationException e) {
            throw new DataAlreadyExistsException("This email is already used!");
        }
    }

    public UserDto getById(Long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) return toDto(maybeUser.get());
        throw new DataNotFoundException(String.format("User with id %d not found!", id));
    }

    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        maybeUser.ifPresent(userRepository::delete);
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail());
    }
}
