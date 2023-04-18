package Entities.Comparators;

import Entities.Artifact;

import java.util.Comparator;

public class Artifact_latency_comparator implements Comparator<Artifact> {

@Override
public int compare(Artifact o1, Artifact o2) {
        return o1.getLatency_cost() - o2.getLatency_cost();
        }

        }