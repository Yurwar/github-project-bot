package edu.kpi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagsData {

    List<String> tags;
}
