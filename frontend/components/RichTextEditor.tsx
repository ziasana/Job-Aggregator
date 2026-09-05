"use client";

import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Link from "@tiptap/extension-link";
import { useEffect, useReducer } from "react";

const buttonClassName = (active: boolean) =>
  `rounded-md px-2.5 py-1.5 text-sm font-semibold transition ${
    active ? "bg-navy text-white" : "text-navy/70 hover:bg-background"
  }`;

/**
 * A plain onClick loses the editor's current text selection first, because
 * the button's mousedown fires (and steals focus) before the click handler
 * runs - so e.g. selecting a word then clicking "Bold" would toggle bold at
 * an empty cursor instead of on the selection. preventDefault on mousedown
 * keeps the selection intact so the click's command applies to it.
 */
function ToolbarButton({
  active,
  onClick,
  children,
}: {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onMouseDown={(e) => e.preventDefault()}
      onClick={onClick}
      className={buttonClassName(active)}
    >
      {children}
    </button>
  );
}

/**
 * Client-side island inside an otherwise server-rendered admin form (see
 * BlogPostForm) - TipTap needs interactivity, but the surrounding <form
 * action={serverAction}> stays a plain HTML form. This component just keeps
 * a hidden <input name="body"> in sync with the editor's HTML output, so
 * the native form submission (no client JS elsewhere in the app) picks it
 * up like any other field.
 */
export default function RichTextEditor({ name, initialContent }: { name: string; initialContent?: string }) {
  // Tiptap's editor instance mutates in place rather than producing a new
  // object per keystroke, so React won't re-render (and the hidden input's
  // value/the toolbar's active-button state would go stale) without this -
  // forces a re-render on every transaction, mirroring Tiptap v2's default.
  const [, forceRerender] = useReducer((c: number) => c + 1, 0);

  const editor = useEditor({
    extensions: [StarterKit, Link.configure({ openOnClick: false })],
    content: initialContent || "<p></p>",
    immediatelyRender: false,
    onTransaction: () => forceRerender(),
  });

  useEffect(() => {
    return () => editor?.destroy();
  }, [editor]);

  if (!editor) {
    return (
      <div className="min-h-[300px] rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy/40">
        Loading editor…
      </div>
    );
  }

  return (
    <div className="rounded-lg border border-black/10 bg-white focus-within:border-brand focus-within:ring-2 focus-within:ring-brand/20">
      <div className="flex flex-wrap gap-1 border-b border-black/10 p-2">
        <ToolbarButton active={editor.isActive("bold")} onClick={() => editor.chain().focus().toggleBold().run()}>
          Bold
        </ToolbarButton>
        <ToolbarButton active={editor.isActive("italic")} onClick={() => editor.chain().focus().toggleItalic().run()}>
          Italic
        </ToolbarButton>
        <ToolbarButton
          active={editor.isActive("heading", { level: 2 })}
          onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}
        >
          H2
        </ToolbarButton>
        <ToolbarButton
          active={editor.isActive("heading", { level: 3 })}
          onClick={() => editor.chain().focus().toggleHeading({ level: 3 }).run()}
        >
          H3
        </ToolbarButton>
        <ToolbarButton active={editor.isActive("bulletList")} onClick={() => editor.chain().focus().toggleBulletList().run()}>
          Bullet list
        </ToolbarButton>
        <ToolbarButton active={editor.isActive("orderedList")} onClick={() => editor.chain().focus().toggleOrderedList().run()}>
          Numbered list
        </ToolbarButton>
        <ToolbarButton active={editor.isActive("blockquote")} onClick={() => editor.chain().focus().toggleBlockquote().run()}>
          Quote
        </ToolbarButton>
        <ToolbarButton
          active={editor.isActive("link")}
          onClick={() => {
            const url = window.prompt("Link URL");
            if (url) {
              editor.chain().focus().setLink({ href: url }).run();
            }
          }}
        >
          Link
        </ToolbarButton>
      </div>
      <EditorContent
        editor={editor}
        className="prose prose-sm max-w-none px-3.5 py-2.5 text-navy [&_.ProseMirror]:min-h-[280px] [&_.ProseMirror]:outline-none"
      />
      <input type="hidden" name={name} value={editor.getHTML()} readOnly />
    </div>
  );
}
