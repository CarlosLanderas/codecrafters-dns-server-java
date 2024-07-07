package com.dnsserver.message;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class QuestionTest {

  @Test
  void encode() {
    var question = new Question("codecrafters.io", (short) 1, (short) 1);
    var questionBuffer = ByteBuffer.allocate(21);
    question.Encode(questionBuffer);

    var qBytes = new byte[]{
        12, //Length
        99, 111, 100, 101, 99, 114, 97, 102, 116, 101, 114, 115, // Name
        2, //  Length
        105, 111, // TLD
        0, // End
        0, 1, // QType
        0, 1}; // QClass


    assertArrayEquals(qBytes, questionBuffer.array());
  }

  @Test
  void parse() {

    var qBytes = new byte[]{
        12, //Length
        99, 111, 100, 101, 99, 114, 97, 102, 116, 101, 114, 115, // Name
        2, //  Length
        105, 111, // TLD
        0, // End
        0, 1, // QType
        0, 1}; // QClass

    var question = Question.Parse(ByteBuffer.wrap(qBytes));

    assertEquals("codecrafters.io", question.domain());
    assertEquals(1, question.QType());
    assertEquals(1, question.QClass());
  }
}
