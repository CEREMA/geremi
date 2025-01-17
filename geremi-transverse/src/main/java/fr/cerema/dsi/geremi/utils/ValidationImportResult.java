package fr.cerema.dsi.geremi.utils;

import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.commons.datastore.DataStore;

public class ValidationImportResult {
    private DataStore dataStore;
    private List<Object> entries;
    private List<String> informativeMessage;
    
    public ValidationImportResult(DataStore dataStore, List<String> informativeMessage) {
        this(dataStore, new ArrayList<>(), informativeMessage);
    }

    public ValidationImportResult(DataStore dataStore, List<Object> entries, List<String> informativeMessage) {
        this.dataStore = dataStore;
        this.entries = entries;
        this.informativeMessage = informativeMessage;
    }

    // Getters et Setters
    public DataStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    } 

    public List<Object> getEntries() {
		return entries;
	}

	public void setEntries(List<Object> entries) {
		this.entries = entries;
	}

	public List<String> getInformativeMessage() {
        return informativeMessage;
    }

    public void setInformativeMessage(List<String> informativeMessage) {
        this.informativeMessage = informativeMessage;
    }
}
