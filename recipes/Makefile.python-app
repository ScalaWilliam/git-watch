PID_FILE = .pid

.PHONY: \
	watch \
	kill \
	run \
	push-run \
	run-pull \
	pull \
	push

kill:
	kill $$(cat $(PID_FILE)) || true
	rm -f $(PID_FILE)

run: kill
	pip install --user -r requirements.txt || true
	{ python -m app & echo $$! > $(PID_FILE); }

pull:
	git pull

# Hack to pull first & then run.
run-pull: pull run
push-run: run-pull pull
push: pull run-pull
