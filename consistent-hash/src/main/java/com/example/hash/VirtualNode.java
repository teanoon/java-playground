package com.example.hash;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class VirtualNode extends Node {

    private Node physicalNode;

}
