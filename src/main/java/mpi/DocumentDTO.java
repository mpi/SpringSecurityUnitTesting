package mpi;

public class DocumentDTO {

    private String username;
    private boolean classified;

    public void setOwnedBy(String username) {
        this.username = username;
    }

    public String getOwnedBy(){
        return username;
    }

    public void setClassified(boolean classified) {
        this.classified = classified;
    }
    
    public boolean isClassified() {
        return classified;
    }
    
    @Override
    public String toString() {
        String type = (classified ? "Classified" : "Regular");
        String owner = (username != null ? username : "no body");
        return String.format("%s document owned by %s", type, owner);
    }
}
