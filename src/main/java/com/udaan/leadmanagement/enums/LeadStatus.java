package com.udaan.leadmanagement.enums;

public enum LeadStatus {
    NEW,             // Initial status when lead is created
    IN_PROGRESS,     // In discussion of deal
    FOLLOW_UP,       // Need more time to convert
    CONVERTED,       // Successfully converted to customer
    LOST            // Lead did not convert
}
