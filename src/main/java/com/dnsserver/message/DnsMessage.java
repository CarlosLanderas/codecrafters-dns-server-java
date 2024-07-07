package com.dnsserver.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record DnsMessage(Header header, List<Question> questions, Answer answer) {

  public static DnsMessage From(byte[] data) {

    var buff = ByteBuffer.wrap(data);
    Header header = Header.Parse(buff);
    List<Question> questions = new ArrayList<>();

    for (int i = 0; i < header.questionCount(); i++) {
      questions.add(Question.Parse(buff));
    }

    Answer answer = Answer.Parse(buff);

    return new DnsMessage(header, questions, answer);
  }

  public byte[] Write() {
    var buff = ByteBuffer.allocate(512);

    var responseHeader  = new Header(
        header.packetIdentifier(),
        true,
        header.operationCode(),
        header.authoritativeAnswer(),
        header.truncation(),
        header.recursionDesired(),
        header.recursionAvailable(),
        header.reserved(),
        (byte) header.responseCode(),
        (short)questions.size(),
        (short) questions.size(),
        header.authorityRecordCount(),
        header.additionalRecordCount()
    );


    responseHeader.Encode(buff);

    for (Question question : questions) {
      question.Encode(buff);
    }

    for (Question question : questions) {
      new Answer(
          question.domain(),
          (short)1,
          (short)1,
          60,
          (short)4,
          new byte[]{8, 8, 8, 8}
      ).Encode(buff);
    }

    return buff.array();
  }
}
