package com.github.shCHO9801.climbing_record_app.community.meeting.controller;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.MeetingParticipationResponse;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.service.MeetingParticipationService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings/{meetingId}/participation")
@RequiredArgsConstructor
public class MeetingParticipationController {

  private final JwtTokenProvider provider;
  private final MeetingParticipationService meetingParticipationService;

  @PostMapping
  public ResponseEntity<MeetingParticipationResponse> joinMeeting(
      @PathVariable Long meetingId,
      @RequestHeader("Authorization") String authorization
  ) {
    String userId = extractUserId(authorization);
    MeetingParticipation participation = meetingParticipationService.participation(userId,
        meetingId);
    MeetingParticipationResponse response = MeetingParticipationResponse.from(participation);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<MeetingParticipationResponse>> getParticipation(
      @PathVariable Long meetingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<MeetingParticipation> participation = meetingParticipationService.getAllParticipation(
        meetingId, pageable);
    PagedResponse<MeetingParticipationResponse> pagedResponse = createPagedResponse(participation);

    return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
  }

  @DeleteMapping("/{participationId}")
  public ResponseEntity<MeetingParticipationResponse> cancelMeeting(
      @PathVariable Long meetingId,
      @PathVariable Long participationId,
      @RequestHeader("Authorization") String authorization
  ) {
    String userId = extractUserId(authorization);
    meetingParticipationService.cancelParticipation(userId, participationId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }

  private PagedResponse<MeetingParticipationResponse> createPagedResponse(
      Page<MeetingParticipation> participation) {
    return PagedResponse.<MeetingParticipationResponse>builder()
        .content(participation.getContent().stream()
            .map(MeetingParticipationResponse::from)
            .toList())
        .page(participation.getNumber())
        .size(participation.getSize())
        .totalElements(participation.getTotalElements())
        .last(participation.isLast())
        .build();
  }
}
