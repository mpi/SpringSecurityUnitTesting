package mpi;

import static mpi.SecurityTesting.destroy;
import static mpi.SecurityTesting.secure;
import static mpi.SecurityTesting.userHasNoRoles;
import static mpi.SecurityTesting.userHasRoles;
import static mpi.SecurityTesting.userPrincipalIs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

@RunWith(MockitoJUnitRunner.class)
public class DocumentControllerSecurityTest {

    @Mock
    private DocumentController target; 

    private DocumentController controller;
    
    @Before
    public void setUp() {

        controller = secure(target);
    }

    @After
    public void tearDown(){
        destroy();
    }

    @Test
    public void shouldAllowDeletingDocumentByAdministator() throws Exception {

        // given:
        DocumentDTO document = aDocument();
        userHasRoles("ADMIN");
        
        // when:
        controller.delete(document);
        
        // then:
        verify(target).delete(document);
    }

    @Test
    public void shouldAllowDeletingOwnDocuments() throws Exception {
        
        // given:
        DocumentDTO document = aDocumentOwnedBy("alice");
        userHasNoRoles();
        userPrincipalIs("alice");
        
        // when:
        controller.delete(document);
        
        // then:
        verify(target).delete(document);
    }

    @Test
    public void shouldDenyDeletingDocumentByNonAdministator() throws Exception {
        
        // given:
        userHasNoRoles();
        
        try{
            // when:
            controller.delete(aDocument());
            failBecauseExceptionWasNotThrown(AccessDeniedException.class);
            
        } catch (Exception e){
            
            // then:
            assertThat(e).isInstanceOf(AccessDeniedException.class);
            verifyZeroInteractions(target);
        }
    }

    @Test
    public void shouldFilterOutClassifiedInformationForRegularUser() throws Exception {
        
        // given:
        DocumentDTO regular = aRegularDocument();
        DocumentDTO classified = aClassifiedDocument();
        
        followingDocumentsAreAvailable(regular, classified);
        userHasNoRoles();
        
        // when:
        List<DocumentDTO> documents = controller.listDocuments();
        
        // then:
        assertThat(documents)
            .contains(regular)
            .doesNotContain(classified);
    }

    @Test
    public void shouldNotFilterOutClassifiedInformationForTopSecurityClearanceUser() throws Exception {
        
        // given:
        DocumentDTO regular = aRegularDocument();
        DocumentDTO classified = aClassifiedDocument();
        
        followingDocumentsAreAvailable(regular, classified);
        userHasRoles("TOP_SECURITY_CLEARANCE");
        
        // when:
        List<DocumentDTO> documents = controller.listDocuments();
        
        // then:
        assertThat(documents).contains(regular, classified);
    }
    
    // --
    
    private void followingDocumentsAreAvailable(DocumentDTO... documents) {
        List<DocumentDTO> copy = new ArrayList<DocumentDTO>(Arrays.asList(documents));
        when(target.listDocuments()).thenReturn(copy);
    }
    
    private DocumentDTO aClassifiedDocument() {
        DocumentDTO document = aDocument();
        document.setClassified(true);
        return document;
    }

    private DocumentDTO aRegularDocument() {
        return aDocument();
    }

    private DocumentDTO aDocument() {
        return new DocumentDTO();
    }

    private DocumentDTO aDocumentOwnedBy(String username) {
        DocumentDTO document = aDocument();
        document.setOwnedBy(username);
        return document;
    }
}


 
