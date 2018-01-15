package util

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

// Helper to generate requestId via base64-encoded UUID
trait RequestIdUtil {

  private val encoder = Base64.getEncoder.withoutPadding()
  private val BytesPerUUID = 16

  // generates 22-char representation of UUID, e.g "69a4ae40-0d48-44e1-8242-1eb4634bb17a" -> "aaSuQA1IROGCQh60Y0uxeg"
  private def encode (uuid: UUID) = {
    val wrapped = ByteBuffer.wrap(new Array(BytesPerUUID))
    wrapped.putLong(uuid.getMostSignificantBits)
    wrapped.putLong(uuid.getLeastSignificantBits)
    encoder.encodeToString(wrapped.array)
  }

  private def generateRequestId = encode(UUID.randomUUID())

  def newRequestId(currentRequestId: Option[String] = None, separator: String = "|"): String =
    List(currentRequestId, Some(generateRequestId)).flatten.mkString(separator)
}

object RequestIdUtil extends RequestIdUtil
