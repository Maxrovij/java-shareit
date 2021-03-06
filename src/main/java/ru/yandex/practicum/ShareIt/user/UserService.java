package ru.yandex.practicum.ShareIt.user;

import org.springframework.beans.factory.annotation.Autowired;
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
    private Long nextId = 0L;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createNew(UserDto userDto) {
        for (User u : userRepository.getAll()) {
            if (u.getEmail().equals(userDto.getEmail()))
                throw new DataAlreadyExistsException("This email is already used!");
            if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
                throw new IncorrectDataException("Missing email!");
            if (!userDto.getEmail().contains("@"))
                throw new IncorrectDataException("Invalid email!");
            if (userDto.getName() == null || userDto.getName().isEmpty())
                throw new IncorrectDataException("Invalid name");
        }
        User user = new User(getNextId(), userDto.getName(), userDto.getEmail());
        return UserMapper.toDto(userRepository.add(user));
    }

    public UserDto patch(UserDto userDto) {
        Optional<User> maybeUser = userRepository.getById(userDto.getId());
        if (maybeUser.isPresent()) {
            User userToEdit = maybeUser.get();
            if (userDto.getName() != null && !userDto.getName().isEmpty()) {
                userToEdit.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                if (userDto.getEmail().contains("@")) {
                    for (User us : userRepository.getAll()) {
                        if (us.getEmail().equals(userDto.getEmail()) && !us.getId().equals(userToEdit.getId())) {
                            throw new DataAlreadyExistsException("This email is already used!");
                        }
                    }
                    userToEdit.setEmail(userDto.getEmail());
                } else throw new IncorrectDataException("Invalid email!");
            }
            return UserMapper.toDto(userRepository.add(userToEdit));
        }
        throw new DataNotFoundException(String.format("User with id %d not found!", userDto.getId()));
    }

    public UserDto getById(Long id) {
        Optional<User> maybeUser = userRepository.getById(id);
        if (maybeUser.isPresent()) return UserMapper.toDto(maybeUser.get());
        else throw new DataNotFoundException(String.format("User with id %d not found!", id));
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    private Long getNextId() {
        nextId = nextId + 1;
        return nextId;
    }
}
