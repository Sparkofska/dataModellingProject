
## Introduction

- [Jonas] some simple words about history and what it is useful for

## Theory (State of the art)

[Jonas]
- refer to Moody
- describe his algorithm
- introduce his terms
    - Transaction, Classification (minimal, maximal hierarchy), ...
    - 2 Operations: Collapse, Aggregation

## Implementation

### Application Design

- What is input, output?
- high level description of algorithm (What is parsed, what is aggregated, ...)
- [Jonas] How the suggestion is produced
- Suggestion can be modified by user (Find agreement with user about output structure)
- [Dusan] What algorithms used for aggregation and collapse
- How the dimensional model is constructed

### Implementation Details

- [Dusan, Jonas] UML Class Diagram of the components (which are there, how they work together)
- Data flow diagram of the pipeline
- which algorithms we use
- more approaches?

### Usage of programm (quite short)

- [Jonas] short manual how to interface with the program

## Experiments, Testing and Evaluation

- show what the tool can
- append some output

## Conclusion

- [Dusan] What we managed to do
- We did the collapsing
- did not managed the aggregation
- did not do the actual star scheme, but altered a bit
    - we left out the connect fact tables in case there are multiple
