WAR="questions-1.0-SNAPSHOT"
WATCHING="src/main/webapp/"
function handle_file_change {
  full_path=$1;
  webapp_path=${full_path:`expr length $WATCHING`}
  final_move_path=./target/$WAR/$webapp_path;

  cp ./$full_path $final_move_path;
  echo "Copying $full_path to $final_move_path";
}
while read output; do
  directory=$(echo $output | cut -d " " -f 1);
  file=$(echo $output | cut -d " " -f 3);
  full_path=$directory$file;
  important=$(echo -n $file | grep -e ".*\.js$\|.*\.css$\|.*\.soy$")
  if [ $important ]; then
    handle_file_change $full_path;
  fi
done;
