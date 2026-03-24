package org.example.execution_node.code_executor.management.files.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.attribute.FileTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
public class FileModel {
    String fileName;
    FileTime fileTime;
}
