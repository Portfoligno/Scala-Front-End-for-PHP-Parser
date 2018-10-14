<?php
require_once 'vendor/autoload.php';

$invalid_inputs = null;
$fatal = false;

$parser_factory_mode = $_GET['parser_factory_mode'];
$parser_factory_mode_constant =
    @constant('PhpParser\ParserFactory::' . $parser_factory_mode);
$source = $_GET['source'];
$json_options = $_GET['json_options'];

if ($parser_factory_mode_constant === null) {
  $invalid_inputs['parser_factory_mode'] = $parser_factory_mode;
  $fatal = true;
}
if ($source === null) {
  $invalid_inputs['source'] = $source;
  $fatal = true;
}
if ($json_options !== null && !ctype_digit($json_options)) {
  $invalid_inputs['json_options'] = $json_options;
  $json_options = 0;
}
$failures = null;

if ($invalid_inputs) {
  $failures['INVALID_INPUTS'] = $invalid_inputs;

  if ($fatal) {
    die(json_encode($failures, $json_options));
  }
}
try {
  $ast = (new PhpParser\ParserFactory)
      ->create($parser_factory_mode_constant)
      ->parse($source);

  if (!$invalid_inputs) {
    echo json_encode($ast, $json_options);
  }
}
catch (PhpParser\Error $e) {
  $failures['PHP_PARSER_ERROR'] = [
    'raw_message' => $e->getRawMessage(),
    'attributes' => $e->getAttributes()
  ];
}

if ($failures) {
  die(json_encode($failures, $json_options));
}
