package com.dnsserver.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record DnsMessage(Header header, List<Question> questions, List<Answer> answers) {

  public static DnsMessage From(byte[] data) {

    var buff = ByteBuffer.wrap(data);
    Header header = Header.Parse(buff);
    List<Question> questions = new ArrayList<>();
    List<Answer> answers = new ArrayList<>();

    for (int i = 0; i < header.questionCount(); i++) {
      questions.add(Question.Parse(buff));
    }

    for (int i = 0; i < header.answerRecordCount(); i++) {
      answers.add(Answer.Parse(buff));
    }

    return new DnsMessage(header, questions, answers);
  }

  public byte[] Decode() {
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

    for (Answer answer : answers) {
      answer.Encode(buff);
    }

    return buff.array();
  }
}
