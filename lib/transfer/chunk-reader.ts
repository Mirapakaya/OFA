// OFA Chunked File Reader
//
// Reads files in chunks using the File API's slice method.
// Never loads an entire large file into memory.
// Uses ReadableStream where available for backpressure.

export const DEFAULT_CHUNK_SIZE = 64 * 1024; // 64 KB

export interface ChunkProgress {
  chunkIndex: number;
  totalChunks: number;
  bytesRead: number;
  totalBytes: number;
}

export interface ChunkResult {
  index: number;
  data: ArrayBuffer;
  offset: number;
  size: number;
  isLast: boolean;
}

/**
 * Read a file as an async generator of chunks.
 * Uses File.slice() to read without loading the whole file into memory.
 * The caller is responsible for processing each chunk before requesting the next.
 */
export async function* readFileChunks(
  file: File,
  chunkSize: number = DEFAULT_CHUNK_SIZE,
): AsyncGenerator<ChunkResult, void, void> {
  const totalChunks = Math.ceil(file.size / chunkSize);
  let offset = 0;
  let index = 0;

  while (offset < file.size) {
    const end = Math.min(offset + chunkSize, file.size);
    const blob = file.slice(offset, end);

    // Read the blob as an ArrayBuffer
    const data = await blob.arrayBuffer();

    yield {
      index,
      data,
      offset,
      size: end - offset,
      isLast: index === totalChunks - 1,
    };

    offset = end;
    index++;
  }
}

/**
 * Compute the total number of chunks for a file.
 */
export function getTotalChunks(fileSize: number, chunkSize: number = DEFAULT_CHUNK_SIZE): number {
  return Math.ceil(fileSize / chunkSize);
}

/**
 * Read a specific chunk from a file by index.
 * Useful for retry scenarios where only one chunk needs to be re-read.
 */
export async function readChunk(
  file: File,
  chunkIndex: number,
  chunkSize: number = DEFAULT_CHUNK_SIZE,
): Promise<ChunkResult> {
  const offset = chunkIndex * chunkSize;
  const end = Math.min(offset + chunkSize, file.size);

  if (offset >= file.size) {
    throw new Error(`Chunk index ${chunkIndex} out of range for file of size ${file.size}`);
  }

  const blob = file.slice(offset, end);
  const data = await blob.arrayBuffer();

  return {
    index: chunkIndex,
    data,
    offset,
    size: end - offset,
    isLast: end >= file.size,
  };
}