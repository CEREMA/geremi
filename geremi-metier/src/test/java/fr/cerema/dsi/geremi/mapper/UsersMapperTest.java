package fr.cerema.dsi.geremi.mapper;

import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.services.ProfilService;
import fr.cerema.dsi.geremi.services.RefEtatService;
import fr.cerema.dsi.geremi.services.RegionService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import fr.cerema.dsi.geremi.services.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
class UsersMapperTest {



  @InjectMocks
  UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  @Mock
  protected RefEtatService refEtatService;

  @Mock
  protected ProfilService profilService;

  @Mock
  protected RegionService regionService;

  User entity = new User();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.entity.setMail("test@test.com");
    this.entity.setId(1L);
  }

  @Test
  void testToDto() {
    UserDTO dto = userMapper.toDto(this.entity);

    assertNotNull(dto);
    assertEquals("test@test.com", dto.getMail());
    assertEquals(1L, dto.getId());
  }

  @Test
  void testToEntity() {
    UserDTO dto = userMapper.toDto(this.entity);

    User entity2 = userMapper.toEntity(dto);

    assertNotNull(entity2);
    assertEquals("test@test.com", entity2.getMail());
    assertEquals(1L, entity2.getId());
  }

}
