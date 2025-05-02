package io.github.redrain0o0.legacyskins.util;

public class ByteSizeFormatter {
    public static String formatByteSizes(long size1, long size2) {
        String commonUnit = determineLargestCommonUnit(size1, size2);
        boolean forceNoDecimals = commonUnit.equals("KB") && 
                                 (size1 >= 10_000 || size2 >= 10_000);
        
        return formatBytes(size1, commonUnit, forceNoDecimals) + "/" + 
               formatBytes(size2, commonUnit, forceNoDecimals);
    }

    private static String determineLargestCommonUnit(long size1, long size2) {
        long maxSize = Math.max(size1, size2);
        return unitFor(maxSize);
    }

    private static String unitFor(long size) {
        if (size < 1_000_000) {          // Less than 1MB -> use KB
            return "KB";
        } else if (size < 1_000_000_000) { // Less than 1GB -> use MB
            return "MB";
        } else if (size < 1_000_000_000_000L) { // Less than 1TB -> use GB
            return "GB";
        } else {
            return "TB";
        }
    }

    public static String formatBytes(long bytes) {
        String unit = unitFor(bytes);
        boolean forceNoDecimals = unit.equals("KB") && bytes >= 10_000;
        return formatBytes(bytes, unit, forceNoDecimals);
    }
    
    private static String formatBytes(long bytes, String unit, boolean forceNoDecimals) {
        double value;
        switch (unit) {
            case "KB":
                value = bytes / 1000.0;
                if (forceNoDecimals || value >= 10) {
                    return String.format("%.0fKB", value);  // 10KB, 150KB (forced no decimals)
                } else {
                    return String.format("%.1fKB", value);  // 5.5KB, 9.9KB
                }
            case "MB":
                value = bytes / 1_000_000.0;
                return String.format("%.2fMB", value);  // 1.50MB
            case "GB":
                value = bytes / 1_000_000_000.0;
                return String.format("%.2fGB", value);  // 0.75GB
            case "TB":
                value = bytes / 1_000_000_000_000.0;
                return String.format("%.2fTB", value);  // 1.25TB
            default:
                value = bytes / 1000.0;
                return String.format("%.0fKB", value);  // Default to KB
        }
    }
}