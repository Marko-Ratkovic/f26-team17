package com.csc340.glowup.enums;

public enum ServiceCategory {
    HAIR_STYLING("Hair Styling"), 
    NAIL_CARE("Nail Care"),
    MAKEUP_ARTISTRY("Makeup Artistry"),
    SKINCARE("Skincare"),
    BROWS_AND_LASHES("Brows & Lashes"),
    HAIR_REMOVAL("Hair Removal");

    private final String displayName;

    ServiceCategory(String displayName) {
        this.displayName = displayName;
    } 

    @Override
    public String toString() {
        return displayName;
    }
    
}
