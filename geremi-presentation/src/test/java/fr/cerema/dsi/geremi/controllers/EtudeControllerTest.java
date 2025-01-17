package fr.cerema.dsi.geremi.controllers;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.mapper.EtudeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudeControllerTest {

	@InjectMocks
	private EtudeController etudeController;

	@Mock
	private EtudeService etudeService;

	@Mock
	private EtudeMapper etudeMapper;

	private Etude etude1;
	private Etude etude2;
	private Etude etude3;

	private List<Etude> etudeList = new ArrayList<>();

	@BeforeEach
	public void setUp() {

		etude1 = new Etude();
		etude1.setId(1L);
		etude1.setNom("Etude 1");
		etude1.setDescription("Description de l'étude 1");
		etude1.setDateCreation(LocalDate.now());
		etude1.setIfSrc(false);
		etude1.setAnneeRef(2023);
		etude1.setAnneeFin(2023);
		etude1.setIfPublic(false);
		etude1.setUser(null);

		etude2 = new Etude();
		etude2.setId(2L);
		etude2.setNom("Etude 2");
		etude2.setDescription("Description de l'étude 2");
		etude2.setDateCreation(LocalDate.now());
		etude2.setIfSrc(true);
		etude2.setAnneeRef(2023);
		etude2.setAnneeFin(2023);
		etude2.setIfPublic(true);
		etude2.setUser(null);

		etude3 = new Etude();
		etude3.setId(3L);
		etude3.setNom("Etude 3");
		etude3.setDescription("Description de l'étude 3");
		etude3.setDateCreation(LocalDate.now());
		etude3.setIfSrc(false);
		etude3.setAnneeRef(2023);
		etude3.setAnneeFin(2023);
		etude3.setIfPublic(false);
		etude3.setUser(null);

		etudeList.add(etude1);
		etudeList.add(etude2);
		etudeList.add(etude3);
	}


	@Test
	@DisplayName("Test deleteEtude() : DELETE /api/etudes/{id}")
	public void testDeleteEtude()  {
		// setup
		Long idToDelete = 1L;
		Etude etudeToDelete = new Etude();
		etudeToDelete.setId(idToDelete);
		when(etudeService.findById(idToDelete)).thenReturn(etudeToDelete);

		// execution
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		etudeController.deleteEtude(idToDelete);

		// verification
		verify(etudeService, times(1)).findById(idToDelete);
		verify(etudeService, times(1)).deleteById(idToDelete);
		verifyNoMoreInteractions(etudeService);
		// assertNull(etudeToDelete.getId());
	}

}
