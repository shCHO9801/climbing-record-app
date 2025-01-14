package com.github.shCHO9801.climbing_record_app.community.meeting.controller;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingResponse;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.GetMeetingResponse;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.UpdateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.service.MeetingService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

  private final JwtTokenProvider provider;
  private final MeetingService meetingService;

  @PostMapping
  public ResponseEntity<CreateMeetingResponse> createMeeting(
      @RequestHeader("Authorization") String authorization,
      @RequestBody CreateMeetingRequest request
  ) {
    String userId = extractUserId(authorization);

    Meeting createdMeeting = meetingService.createMeeting(userId, request);

    CreateMeetingResponse response = CreateMeetingResponse.from(createdMeeting);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<GetMeetingResponse>> getMeetings(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<Meeting> meetingPage = meetingService.getAllMeetings(pageable);

    PagedResponse<GetMeetingResponse> response = createPagedResponse(meetingPage);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{meetingId}")
  public ResponseEntity<GetMeetingResponse> updateMeeting(
      @RequestHeader("Authorization") String authorization,
      @PathVariable Long meetingId,
      @RequestBody UpdateMeetingRequest request
  ) {
    String userId = extractUserId(authorization);

    Meeting meeting = meetingService.updateMeeting(userId, meetingId, request);

    return ResponseEntity.status(HttpStatus.OK).body(GetMeetingResponse.from(meeting));
  }

  @DeleteMapping("/{meetingId}")
  public ResponseEntity<Void> deleteMeeting(
      @RequestHeader("Authorization") String authorization,
      @PathVariable Long meetingId
  ) {
    String userId = extractUserId(authorization);

    meetingService.deleteMeeting(userId, meetingId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }

  private PagedResponse<GetMeetingResponse> createPagedResponse(Page<Meeting> meetings) {
    return PagedResponse.<GetMeetingResponse>builder()
        .content(meetings.getContent().stream()
            .map(GetMeetingResponse::from)
            .toList())
        .page(meetings.getNumber())
        .size(meetings.getSize())
        .totalElements(meetings.getTotalElements())
        .last(meetings.isLast())
        .build();
  }
}
