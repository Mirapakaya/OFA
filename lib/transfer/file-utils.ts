// OFA File Utilities
//
// File validation, size formatting, and type checking.
// No file is ever uploaded to a server — these utilities
// operate entirely in the browser.

/** Maximum total transfer size: 10 GB */
export const MAX_TOTAL_SIZE = 10 * 1024 * 1024 * 1024;

/** Maximum single file size: 5 GB */
export const MAX_FILE_SIZE = 5 * 1024 * 1024 * 1024;

/** Maximum number of files per transfer */
export const MAX_FILE_COUNT = 100;

export interface FileValidationResult {
  valid: boolean;
  error?: string;
}

/** Format byte size for human display */
export function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
  if (bytes < 1073741824) return (bytes / 1048576).toFixed(1) + " MB";
  return (bytes / 1073741824).toFixed(1) + " GB";
}

/** Format transfer speed */
export function formatSpeed(bytesPerSecond: number): string {
  return formatSize(bytesPerSecond) + "/s";
}

/** Format remaining time estimate */
export function formatETA(seconds: number): string {
  if (!isFinite(seconds) || seconds < 0) return "";
  if (seconds < 60) return `about ${Math.ceil(seconds)} second${Math.ceil(seconds) !== 1 ? "s" : ""}`;
  if (seconds < 3600) return `about ${Math.ceil(seconds / 60)} minute${Math.ceil(seconds / 60) !== 1 ? "s" : ""}`;
  return `about ${Math.ceil(seconds / 3600)} hour${Math.ceil(seconds / 3600) !== 1 ? "s" : ""}`;
}

/** Validate a single file */
export function validateFile(file: File): FileValidationResult {
  if (file.size > MAX_FILE_SIZE) {
    return {
      valid: false,
      error: `${file.name} exceeds the ${formatSize(MAX_FILE_SIZE)} limit`,
    };
  }
  return { valid: true };
}

/** Validate a set of files for transfer */
export function validateFiles(files: File[]): FileValidationResult {
  if (files.length === 0) {
    return { valid: false, error: "No files selected" };
  }

  if (files.length > MAX_FILE_COUNT) {
    return {
      valid: false,
      error: `Maximum ${MAX_FILE_COUNT} files per transfer`,
    };
  }

  const totalSize = files.reduce((sum, f) => sum + f.size, 0);
  if (totalSize > MAX_TOTAL_SIZE) {
    return {
      valid: false,
      error: `Total size exceeds the ${formatSize(MAX_TOTAL_SIZE)} limit`,
    };
  }

  for (const file of files) {
    const result = validateFile(file);
    if (!result.valid) return result;
  }

  return { valid: true };
}

/** Get file extension (lowercase, without dot) */
export function getExtension(name: string): string {
  const dot = name.lastIndexOf(".");
  if (dot < 0 || dot === name.length - 1) return "";
  return name.slice(dot + 1).toLowerCase();
}

/** Check if a filename looks like a folder (has path separator) */
export function hasPathSeparator(name: string): boolean {
  return name.includes("/") || name.includes("\\");
}

/** Sanitize a filename for safe display */
export function sanitizeFileName(name: string): string {
  // Remove path components, keep only the base name
  const base = name.split(/[/\\]/).pop() ?? name;
  // Truncate extremely long names
  if (base.length > 255) return base.slice(0, 252) + "...";
  return base;
}