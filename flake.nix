{
  description = "Java 20 development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/24.05";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
        };

        java = pkgs.jdk20;
      in {
        devShells.default = pkgs.mkShell {
          packages = [ java ];

          shellHook = ''
            echo "Java version: $(java -version 2>&1 | head -n 1)"
            echo "You're now in a Java 20 dev environment!"
          '';
        };
      });
}
