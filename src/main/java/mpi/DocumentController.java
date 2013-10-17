package mpi;

import java.util.Collections;
import java.util.List;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

public class DocumentController {

    @PreAuthorize("hasRole('ADMIN') or #document.ownedBy == principal")
    public void delete(DocumentDTO document) {
     
        System.out.println("Very complex logic... and hard to get into test harness.");
        
    }

    @PostFilter("hasRole('TOP_SECURITY_CLEARANCE')  or not filterObject.classified")
    public List<DocumentDTO> listDocuments() {

        System.out.println("Very complex logic... and hard to get into test harness.");
        
        return Collections.emptyList();
    }
    
}
