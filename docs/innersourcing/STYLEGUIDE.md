# Styleguide

## 1. Globals

We do not use global variables. If you are in a pipeline and you need to pass variables around, use function-parameters. And if this is not enough: use `env`.

## 2. Private functions

Private functions are prefixed with an underscore (`_`) as Groovy has no other way to indicate a private function.

#### Example

```
def _privateFunction() { }
```

## 3. Explicit return types

Functions have explicit return types where possible.

#### Example

```
String myFunctionWithResult() {
    return 'ThisIsMyResult'
}
```

## 4. Explicit typing

We prefer explicit typing where possible.

#### Example
```
void myFunction(String input) {
    print(input)
}

myFunction('myString')
```

## 5. Usage of Quotes (prefer single-quotes)
By default we always use single-quotes, unless we need interpolated variables, then we use double-quotes.

#### Example

```
String variable = 'test123'
print "My variable is: $variable."
```
will output:
`My variable is: test123.`

## 6. Properties / fields

There are several ways to retrieve a property:

* `Object.property`
* `Object['property']`
* `Object.get('property')`

We use the first method (`Object.property`) where possible.

NB When you have dotted properties (```user.fullName```) there's no other way than to use the 2nd option (```Object['User.person']```)

## 7. Assert

We use asserts where possible. Aim to make your errors explicit.

#### Example

```
String myFunction(String input1, String input2) {
    // Use case: input1 is required and checked, input2 is optional
    assert input1: 'I need input1 because my return command requires it'
    assert input1.size() > 1: 'I need input1 to have a string with length > 1'
    String result = "$input1 has length"
    
    //input2 is optional
    if(input2) {
        result += "; input2 has value $input2"
    }
    return result
}
```